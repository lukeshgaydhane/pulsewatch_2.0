package com.pulsewatch.monitoring.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MetricsResponse {
    private String host;
    private String metricType;
    private List<MetricPoint> data;
    
    @Data
    public static class MetricPoint {
        private LocalDateTime timestamp;
        private Double value;
        private String unit;
    }
} 