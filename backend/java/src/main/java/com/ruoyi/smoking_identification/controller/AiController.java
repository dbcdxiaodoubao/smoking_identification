package com.ruoyi.smoking_identification.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.smoking_identification.domain.vo.LevelListVo;
import com.ruoyi.smoking_identification.domain.vo.LocationListVo;
import com.ruoyi.smoking_identification.domain.vo.TimeListVo;
import com.ruoyi.smoking_identification.domain.vo.TrendListVo;
import com.ruoyi.smoking_identification.service.IIncidentService;
import com.ruoyi.smoking_identification.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/ai")
@Api(tags = "AI接口")
public class AiController {

    private String requireApiKey() {
        String apiKey = System.getenv("ZHIPUAI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("ZHIPUAI_API_KEY is not configured");
        }
        return apiKey;
    }

    @Autowired
    private IIncidentService incidentService;

    @GetMapping("/{flag}")
    @ApiOperation("分析数据（传入1为分析近24小时传入2为分析近7天）")
    public R analysis(@Validated @PathVariable Integer flag) throws JsonProcessingException {


        if (flag == null || (flag != 1 && flag != 2)) {
            return R.fail("参数错误！type只能为1（近24小时）或2（近7天）");
        }

        List<TrendListVo> trendListVos = incidentService.listTrend();
        List<LocationListVo> locationListVos = incidentService.listLocation();
        List<TimeListVo> timeListVos = incidentService.listTime();
        List<LevelListVo> levelListVos = incidentService.listLevel();

        String content = "趋势数据：{"+ JSON.toJSONString(trendListVos)+"}"
                +"位置数据：{"+ JSON.toJSONString(locationListVos)+"}"
                +"时间数据：{"+JSON.toJSONString(timeListVos)+"}"
                +"等级数据：{"+ JSON.toJSONString(levelListVos)+"}";


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)    // 读取超时设长一些
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        if (flag == 1){
            message.put("content", "请你帮我分析近24小时的数据，直接得出结论不需要给我代码，数据如下"+content+"其中的incidentCount代表次数" +
                    "，level1到3代表危险等级低中高");
        }
        if(flag == 2){
            message.put("content", "请你帮我分析近七天的数据，直接得出结论不需要给我代码，数据如下"+content+"其中的incidentCount代表次数" +
                    "，level1到3代表危险等级低中高");  // 先用简短内容测试
        }

        System.out.println("AI调用中");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "glm-4-flash");  // 使用更快的模型测试
        requestBody.put("messages", Collections.singletonList(message));

        String jsonBody = mapper.writeValueAsString(requestBody);
        MediaType json = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, json);

        Request request = new Request.Builder()
                .url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .addHeader("Authorization", "Bearer " + requireApiKey())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseStr = response.body().string();

            // 2. 解析JSON字符串为JSONObject（使用fastjson2，和你现有依赖一致）
            JSONObject responseJson = JSON.parseObject(responseStr);

            // 3. 逐层提取content：choices[0].message.content
            // 先获取choices数组，取第一个元素
            JSONObject choice = responseJson.getJSONArray("choices").getJSONObject(0);
            // 再获取message对象
            JSONObject messageObj = choice.getJSONObject("message");
            // 最后提取content字段
            String aiContent = messageObj.getString("content");

            // 4. 只返回content内容
            return R.ok(aiContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
