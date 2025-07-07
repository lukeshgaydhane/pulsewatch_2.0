package com.pulsewatch.dashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "prediction-service", url = "http://localhost:8082")
public interface PredictionClient {
    
    @GetMapping("/ai/predict")
    Map<String, Object> getPredictionSummary();
    
    @GetMapping("/ai/anomalies")
    Map<String, Object> getAnomalySummary();
    
    @PostMapping("/ai/predict")
    Map<String, Object> predictAnomaly(@RequestBody Map<String, Object> data);
} 