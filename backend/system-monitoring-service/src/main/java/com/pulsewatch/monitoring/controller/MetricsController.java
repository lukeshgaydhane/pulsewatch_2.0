package com.pulsewatch.monitoring.controller;

import com.pulsewatch.monitoring.dto.MetricsRequest;
import com.pulsewatch.monitoring.dto.MetricsResponse;
import com.pulsewatch.monitoring.model.SystemMetrics;
import com.pulsewatch.monitoring.service.MetricsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetricsController {

    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);

    private final MetricsService metricsService;

    @PostMapping
    public ResponseEntity<String> saveMetrics(@Valid @RequestBody MetricsRequest request) {
        log.info("Received metrics request for host: {}", request.getHost());
        metricsService.saveMetrics(request);
        return ResponseEntity.ok("Metrics saved successfully");
    }

    @GetMapping("/cpu")
    public ResponseEntity<List<SystemMetrics>> getCpuMetrics(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusHours(1)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        List<SystemMetrics> metrics = metricsService.getCpuMetrics(startTime);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/memory")
    public ResponseEntity<List<SystemMetrics>> getMemoryMetrics(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusHours(1)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        List<SystemMetrics> metrics = metricsService.getMemoryMetrics(startTime);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/disk")
    public ResponseEntity<List<SystemMetrics>> getDiskMetrics(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusHours(1)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        List<SystemMetrics> metrics = metricsService.getDiskMetrics(startTime);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/network")
    public ResponseEntity<List<SystemMetrics>> getNetworkMetrics(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusHours(1)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        List<SystemMetrics> metrics = metricsService.getNetworkMetrics(startTime);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/{metricType}")
    public ResponseEntity<MetricsResponse> getMetricsByType(
            @PathVariable String metricType,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusHours(1)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        MetricsResponse response = metricsService.getMetricsByType(metricType, startTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{metricType}/average")
    public ResponseEntity<Double> getAverageMetricValue(
            @PathVariable String metricType,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusHours(1)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        Double average = metricsService.getAverageMetricValue(metricType, startTime);
        return ResponseEntity.ok(average);
    }
} 