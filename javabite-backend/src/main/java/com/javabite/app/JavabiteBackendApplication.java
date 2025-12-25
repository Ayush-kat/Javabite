package com.javabite.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavabiteBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavabiteBackendApplication.class, args);
    }
}