package com.pulsewatch.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SystemMonitoringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemMonitoringServiceApplication.class, args);
    }
} 