package com.ruoyi.smoking_identification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.access-path}")
    private String accessPath;

    /**
     * 静态资源映射：将本地图片路径映射为URL访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射规则：访问 http://localhost:8080/images/xxx.jpg 对应本地 D:/upload/images/xxx.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath);

        registry.addResourceHandler("/images/face/**")
                .addResourceLocations("file:" + uploadPath + "face/");
    }
}