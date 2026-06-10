package com.ruoyi.smoking_identification;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class QuickStart {
    public static void main(String[] args) throws Exception {
        // 增加超时时间
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)    // 读取超时设长一些
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "你好");  // 先用简短内容测试

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "glm-4-flash");  // 使用更快的模型测试
        requestBody.put("messages", Collections.singletonList(message));

        String jsonBody = mapper.writeValueAsString(requestBody);
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .addHeader("Authorization", "Bearer ZHIPUAI_API_KEY")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}