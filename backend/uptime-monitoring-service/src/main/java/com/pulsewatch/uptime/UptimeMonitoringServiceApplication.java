package com.pulsewatch.uptime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UptimeMonitoringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UptimeMonitoringServiceApplication.class, args);
    }
} 