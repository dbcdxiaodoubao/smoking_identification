package com.ruoyi.smoking_identification;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ruoyi.smoking_identification.mapper")
public class SmokingIdentificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmokingIdentificationApplication.class, args);
    }

}
