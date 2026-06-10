package com.ruoyi.smoking_identification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局跨域配置（解决前后端分离跨域请求问题）
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. 配置跨域信息
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有前端 Origin（开发环境用 *，生产环境替换为具体前端域名，如 http://localhost:8081）
        config.addAllowedOriginPattern("*");
        // 允许的请求方法（POST/GET/OPTIONS 等，适配推流的 POST 请求）
        config.addAllowedMethod("*");
        // 允许的请求头（适配前端的 Content-Type: application/octet-stream）
        config.addAllowedHeader("*");
        // 允许携带 Cookie（前后端分离场景可选，推流场景一般不需要，开启不影响）
        config.setAllowCredentials(true);
        // 预检请求（OPTIONS）的缓存时间（3600 秒 = 1 小时，减少 OPTIONS 请求次数）
        config.setMaxAge(3600L);

        // 2. 配置哪些 URL 生效（/* 表示所有接口都允许跨域）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 所有接口都应用跨域配置

        // 3. 返回 CorsFilter 过滤器
        return new CorsFilter(source);
    }
}