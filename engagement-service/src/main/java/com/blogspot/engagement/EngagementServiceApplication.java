package com.blogspot.engagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EngagementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EngagementServiceApplication.class, args);
    }
}


