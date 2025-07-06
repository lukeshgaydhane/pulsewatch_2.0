package com.pulsewatch.monitoring.service;

import com.pulsewatch.monitoring.dto.MetricsRequest;
import com.pulsewatch.monitoring.dto.MetricsResponse;
import com.pulsewatch.monitoring.model.SystemMetrics;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsService {
    void saveMetrics(MetricsRequest request);
    List<SystemMetrics> getCpuMetrics(LocalDateTime startTime);
    List<SystemMetrics> getMemoryMetrics(LocalDateTime startTime);
    List<SystemMetrics> getDiskMetrics(LocalDateTime startTime);
    List<SystemMetrics> getNetworkMetrics(LocalDateTime startTime);
    MetricsResponse getMetricsByType(String metricType, LocalDateTime startTime);
    Double getAverageMetricValue(String metricType, LocalDateTime startTime);
} 