package com.pulsewatch.monitoring.service.impl;

import com.pulsewatch.monitoring.dto.MetricsRequest;
import com.pulsewatch.monitoring.dto.MetricsResponse;
import com.pulsewatch.monitoring.model.SystemMetrics;
import com.pulsewatch.monitoring.repository.SystemMetricsRepository;
import com.pulsewatch.monitoring.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private static final Logger log = LoggerFactory.getLogger(MetricsServiceImpl.class);

    private final SystemMetricsRepository metricsRepository;

    @Override
    public void saveMetrics(MetricsRequest request) {
        log.info("Saving metrics for host: {}", request.getHost());
        
        List<SystemMetrics> metrics = request.getMetrics().stream()
                .map(metricData -> {
                    SystemMetrics metric = new SystemMetrics();
                    metric.setHost(request.getHost());
                    metric.setTimestamp(request.getTimestamp());
                    metric.setMetricType(SystemMetrics.MetricType.valueOf(metricData.getMetricType().toUpperCase()));
                    metric.setMetricName(metricData.getMetricName());
                    metric.setMetricValue(metricData.getMetricValue());
                    metric.setUnit(metricData.getUnit());
                    metric.setAdditionalData(metricData.getAdditionalData());
                    return metric;
                })
                .collect(Collectors.toList());
        
        metricsRepository.saveAll(metrics);
        log.info("Saved {} metrics for host: {}", metrics.size(), request.getHost());
    }

    @Override
    public List<SystemMetrics> getCpuMetrics(LocalDateTime startTime) {
        return metricsRepository.findByMetricTypeAndTimestampAfter(SystemMetrics.MetricType.CPU, startTime);
    }

    @Override
    public List<SystemMetrics> getMemoryMetrics(LocalDateTime startTime) {
        return metricsRepository.findByMetricTypeAndTimestampAfter(SystemMetrics.MetricType.MEMORY, startTime);
    }

    @Override
    public List<SystemMetrics> getDiskMetrics(LocalDateTime startTime) {
        return metricsRepository.findByMetricTypeAndTimestampAfter(SystemMetrics.MetricType.DISK, startTime);
    }

    @Override
    public List<SystemMetrics> getNetworkMetrics(LocalDateTime startTime) {
        return metricsRepository.findByMetricTypeAndTimestampAfter(SystemMetrics.MetricType.NETWORK, startTime);
    }

    @Override
    public MetricsResponse getMetricsByType(String metricType, LocalDateTime startTime) {
        SystemMetrics.MetricType type = SystemMetrics.MetricType.valueOf(metricType.toUpperCase());
        List<SystemMetrics> metrics = metricsRepository.findByMetricTypeAndTimestampAfter(type, startTime);
        
        MetricsResponse response = new MetricsResponse();
        response.setMetricType(metricType);
        response.setData(metrics.stream()
                .map(this::mapToMetricPoint)
                .collect(Collectors.toList()));
        
        return response;
    }

    @Override
    public Double getAverageMetricValue(String metricType, LocalDateTime startTime) {
        SystemMetrics.MetricType type = SystemMetrics.MetricType.valueOf(metricType.toUpperCase());
        return metricsRepository.getAverageMetricValue(type, startTime);
    }

    private MetricsResponse.MetricPoint mapToMetricPoint(SystemMetrics metric) {
        MetricsResponse.MetricPoint point = new MetricsResponse.MetricPoint();
        point.setTimestamp(metric.getTimestamp());
        point.setValue(metric.getMetricValue());
        point.setUnit(metric.getUnit());
        return point;
    }
} 