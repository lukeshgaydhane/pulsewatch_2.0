package com.pulsewatch.monitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MetricsRequest {
    
    @NotBlank(message = "Host is required")
    private String host;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    @NotNull(message = "Metrics are required")
    private List<MetricData> metrics;
    
    @Data
    public static class MetricData {
        @NotBlank(message = "Metric type is required")
        private String metricType;
        
        @NotBlank(message = "Metric name is required")
        private String metricName;
        
        @NotNull(message = "Metric value is required")
        private Double metricValue;
        
        private String unit;
        private String additionalData;
    }
} 