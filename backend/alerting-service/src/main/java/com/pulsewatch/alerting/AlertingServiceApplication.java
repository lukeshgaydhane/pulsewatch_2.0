package com.pulsewatch.alerting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertingServiceApplication.class, args);
    }
} 