package com.pulsewatch.dashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "alert-service", url = "http://localhost:8081")
public interface AlertClient {
    
    @GetMapping("/alerts/latest")
    List<Map<String, Object>> getLatestAlerts();
    
    @GetMapping("/alerts")
    List<Map<String, Object>> getAllAlerts(@RequestParam(value = "limit", defaultValue = "10") Integer limit);
    
    @GetMapping("/alerts/count")
    Map<String, Object> getAlertCount();
} 