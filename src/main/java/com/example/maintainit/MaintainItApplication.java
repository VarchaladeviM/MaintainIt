package com.example.maintainit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaintainItApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaintainItApplication.class, args);
    }
}