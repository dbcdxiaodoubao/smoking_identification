package com.ruoyi.smoking_identification.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Knife4j 配置类：自定义主页信息 + 仅扫描带有 @Api 注解的控制器
 */
@Configuration
@EnableSwagger2 // 启用 Swagger2 核心功能
@EnableKnife4j // 启用 Knife4j 增强功能（必须添加，否则无 Knife4j 样式）
public class Knife4jConfig {

    /**
     * 构建 Docket 对象，配置接口文档核心信息
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 1. 配置主页信息（ApiInfo）
                .apiInfo(buildApiInfo())
                .select()
                // 2. 关键配置：仅扫描 类上 带有 @Api 注解的控制器
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                // 3. 匹配所有路径（可按需调整，比如 PathSelectors.ant("/api/**") 仅匹配/api下的接口）
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(buildGlobalTokenRequestParameter());
    }

    private List<RequestParameter> buildGlobalTokenRequestParameter() {
        List<RequestParameter> parameters = new ArrayList<>();
        RequestParameter tokenParam = new RequestParameterBuilder()
                .name("Authorization")
                .description("接口认证 Token，格式：{你的Token值}")
                .in(ParameterType.HEADER)
                .required(true)
                .build();
        parameters.add(tokenParam);
        return parameters;
    }

    /**
     * 手动构建 ApiInfo，自定义 Knife4j 主页信息
     */
    private ApiInfo buildApiInfo() {

        Contact contact = new Contact(
                "zzh", // 开发者姓名
                null, // 开发者网址（可选）
                null // 开发者邮箱（可选）
        );

        return new ApiInfoBuilder()
                .title("香烟识别系统-RESTful API 文档") // 主页大标题
                .description("本文档仅展示带有 @Api 注解的控制器接口，包含请求参数、响应结果、错误码说明") // 文档描述
                .version("v0.0.1") // 接口版本号
                .contact(contact) // 开发者信息
                .build();
    }
}