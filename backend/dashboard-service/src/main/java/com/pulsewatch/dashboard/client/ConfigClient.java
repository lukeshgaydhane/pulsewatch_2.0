package com.pulsewatch.dashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "config-service", url = "http://localhost:8085")
public interface ConfigClient {
    
    @GetMapping("/config/{userId}")
    Map<String, Object> getConfigByUserId(@PathVariable("userId") Long userId);
    
    @GetMapping("/thresholds")
    List<Map<String, Object>> getAllThresholds();
    
    @GetMapping("/thresholds/{userId}")
    List<Map<String, Object>> getThresholdsByUserId(@PathVariable("userId") Long userId);
} 