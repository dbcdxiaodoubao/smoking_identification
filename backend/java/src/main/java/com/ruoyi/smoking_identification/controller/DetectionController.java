package com.ruoyi.smoking_identification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.smoking_identification.domain.entity.Detection;
import com.ruoyi.smoking_identification.domain.query.CameraSubmitQuery;
import com.ruoyi.smoking_identification.domain.query.IncidentCreateQuery;
import com.ruoyi.smoking_identification.service.IIncidentService;
import com.ruoyi.smoking_identification.service.IStudentService;
import com.ruoyi.smoking_identification.utils.EmailUtil;
import com.ruoyi.smoking_identification.utils.UploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PreDestroy;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@RequestMapping("/detection")
@Api(tags = "识别接口")
public class DetectionController {

    // Python 服务地址（见下一步）
    private static final String PYTHON_DETECT_URL = "http://localhost:5000/detect";

    private static final String PYTHON_FACE_MATCH_URL = "http://localhost:5000/find_similar_face";

    private static final long COOL_DOWN_TIME = 1 * 15 * 1000L;

    private static final Map<Long, Long> CAMERA_LAST_SAVE_TIME = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Autowired
    private UploadUtil uploadUtil;

    @Autowired
    private IIncidentService incidentService;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private EmailUtil emailUtil;

    @PostMapping
    @ApiOperation("识别接口")
    public ResponseEntity<List<Detection>> detect(@RequestParam("image") MultipartFile imageFile
            , @RequestParam("cameraSubmitQuery") String cameraSubmitQueryStr ) {
        try {
            // 手动解析 JSON 字符串为 CameraSubmitQuery 对象
            ObjectMapper mapper1 = new ObjectMapper();
            CameraSubmitQuery cameraSubmitQuery = mapper1.readValue(cameraSubmitQueryStr, CameraSubmitQuery.class);

            // 1. 读取图片并转换为Base64
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 2. 构造JSON请求体
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(
                    Collections.singletonMap("image", base64Image)
            );

            // 3. 发送POST请求到Python YOLO服务
            List<Detection> detections = null;
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(PYTHON_DETECT_URL);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(jsonPayload));

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        String responseBody = EntityUtils.toString(entity);

                        // 4. 解析识别结果
                        Detection[] detectionsArray = mapper.readValue(responseBody, Detection[].class);
                        detections = Arrays.asList(detectionsArray);
                    } else {
                        return ResponseEntity.status(500).body(null);
                    }
                }
            }

            // 5. 判断是否识别到香烟，且满足冷却条件
            if (isCigaretteDetected(detections) && canSaveImage(cameraSubmitQuery.getCameraId())) {
                // 更新该摄像头的最后保存时间
                CAMERA_LAST_SAVE_TIME.put(cameraSubmitQuery.getCameraId(), System.currentTimeMillis());
                // 若需要CameraSubmitQuery的其他字段，可传入
                String imageSaveUrl = uploadUtil.uploadImage(imageFile, cameraSubmitQuery);
                System.out.println("摄像头[" + cameraSubmitQuery.getCameraId()
                        + "]识别到香烟，图片已保存，访问地址：" + imageSaveUrl);

                IncidentCreateQuery incidentCreateQuery = new IncidentCreateQuery();
                incidentCreateQuery.setCameraId(cameraSubmitQuery.getCameraId());
                incidentCreateQuery.setLocation(cameraSubmitQuery.getLocation());
                incidentCreateQuery.setPictureUrl(imageSaveUrl);

                incidentService.insertIncident(incidentCreateQuery);

                LocalDateTime now = LocalDateTime.now();
                // 2. 格式化时间为指定字符串（如：2025-12-27 15:30:25）
                String currentTime = now.format(DATE_FORMATTER);


                emailUtil.sendSimpleEmail("change-me@example.com","您好，监控有识别到疑是抽烟行为请尽快处理"
                        ,"时间："+currentTime+"  危险等级为（1-3代表低-高）："+cameraSubmitQuery.getLevel()+"  地点："+
                        cameraSubmitQuery.getLocation());


            } else if (isCigaretteDetected(detections) && !canSaveImage(cameraSubmitQuery.getCameraId())) {
                System.out.println("摄像头[" + cameraSubmitQuery.getCameraId()
                        + "]识别到香烟，但未满足5分钟冷却条件，暂不保存图片");
            } else {
                System.out.println("未识别到香烟，无需保存图片");
                return ResponseEntity.ok(null);
            }

            return ResponseEntity.ok(detections);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 调用Python人脸相似度匹配接口（新增核心方法）
     * @param imageBytes 图片字节数组
     * @return 人脸识别结果Map，null表示调用失败
     */
    private Map<String, Object> callFaceMatchService(byte[] imageBytes) {
        CloseableHttpClient httpClient = null;
        try {
            // 1. 将图片转为Base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 2. 构造请求体
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(
                    Collections.singletonMap("image", base64Image)
            );

            // 3. 创建HTTP客户端并发送请求
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(PYTHON_FACE_MATCH_URL);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));

            // 4. 执行请求并处理响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    String responseBody = EntityUtils.toString(entity, "UTF-8");

                    // 5. 解析JSON响应为Map
                    return mapper.readValue(responseBody, Map.class);
                } else {
                    System.err.println("人脸识别接口调用失败，状态码：" + statusCode);
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("调用人脸识别接口异常：" + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            // 关闭HTTP客户端
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断是否识别到香烟
     * @param detections YOLO识别结果列表
     * @return true=识别到香烟，false=未识别到
     */
    private boolean isCigaretteDetected(List<Detection> detections) {
        // 避免空指针异常
        if (detections == null || detections.isEmpty()) {
            return false;
        }

        for (Detection detection : detections) {
            detection.confidence+=0.06;
            System.out.println(detection.class_name+" "+detection.confidence);
        }

        return detections.stream()
                .anyMatch(d -> "smoke".equals(d.getClass_name()) // 标签判断
                        && d.getConfidence() > 0.45); // 置信度判断
    }

    /**
     * 判断是否满足图片保存条件（冷却时间判断）
     * @param cameraId 摄像头ID
     * @return true=可保存，false=不可保存
     */
    private boolean canSaveImage(Long cameraId) {
        long currentTime = System.currentTimeMillis();
        // 获取该摄像头最后保存时间
        Long lastSaveTime = CAMERA_LAST_SAVE_TIME.get(cameraId);
        // 条件1：首次保存（无记录）；条件2：当前时间与最后保存时间间隔超过5分钟
        return lastSaveTime == null || (currentTime - lastSaveTime) > COOL_DOWN_TIME;
    }

    // 程序关闭时关闭定时任务（避免内存泄漏）
    @PreDestroy
    public void destroy() {
        SCHEDULER.shutdown();
    }

}