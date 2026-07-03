package com.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(IotHubApplication.class, args);
    }
}
