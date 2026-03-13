package com.testgenie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestGenieApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestGenieApplication.class, args);
        System.out.println("TestGenie AI Backend is running on http://localhost:8080");
    }
}
