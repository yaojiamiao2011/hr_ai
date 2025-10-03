package com.example.hrai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.hrai.entity")
public class HrAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrAiApplication.class, args);
    }
}