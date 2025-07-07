package com.pulsewatch.dashboard.controller;

import com.pulsewatch.dashboard.service.DashboardService;
import com.pulsewatch.dashboard.service.DatabaseService;
import com.pulsewatch.dashboard.entity.DashboardMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;
    private final DatabaseService databaseService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary(@RequestParam Long userId) {
        try {
            log.info("Fetching dashboard summary for user: {}", userId);
            Map<String, Object> result = dashboardService.getDashboardSummary(userId);
            
            // Add database metrics to the summary
            List<Map<String, Object>> dbMetrics = databaseService.getDashboardSummaryFromDB(userId);
            result.put("databaseMetrics", dbMetrics);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching dashboard summary for user {}: {}", userId, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch dashboard data");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Map<String, Object>>> getAlerts(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            log.info("Fetching alerts with limit: {}", limit);
            List<Map<String, Object>> alerts = dashboardService.getAlerts(limit);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            log.error("Error fetching alerts: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/predictions")
    public ResponseEntity<Map<String, Object>> getPredictions() {
        try {
            log.info("Fetching prediction summary");
            Map<String, Object> predictions = dashboardService.getPredictions();
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            log.error("Error fetching predictions: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/anomalies")
    public ResponseEntity<Map<String, Object>> getAnomalies() {
        try {
            log.info("Fetching anomaly summary");
            Map<String, Object> anomalies = dashboardService.getAnomalies();
            return ResponseEntity.ok(anomalies);
        } catch (Exception e) {
            log.error("Error fetching anomalies: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/config/{userId}")
    public ResponseEntity<Map<String, Object>> getUserConfig(@PathVariable Long userId) {
        try {
            log.info("Fetching config for user: {}", userId);
            Map<String, Object> config = dashboardService.getUserConfig(userId);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("Error fetching config for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Database-specific endpoints
    @GetMapping("/metrics/{userId}")
    public ResponseEntity<List<DashboardMetrics>> getUserMetrics(@PathVariable Long userId) {
        try {
            log.info("Fetching metrics for user: {}", userId);
            List<DashboardMetrics> metrics = databaseService.getMetricsByUserId(userId);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error fetching metrics for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/metrics/{userId}/{metricType}")
    public ResponseEntity<List<DashboardMetrics>> getUserMetricsByType(
            @PathVariable Long userId, 
            @PathVariable String metricType) {
        try {
            log.info("Fetching metrics for user: {} and type: {}", userId, metricType);
            List<DashboardMetrics> metrics = databaseService.getMetricsByUserIdAndType(userId, metricType);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error fetching metrics for user {} and type {}: {}", userId, metricType, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/metrics")
    public ResponseEntity<DashboardMetrics> saveMetric(@RequestBody DashboardMetrics metric) {
        try {
            log.info("Saving metric for user: {}", metric.getUserId());
            DashboardMetrics savedMetric = databaseService.saveMetric(metric);
            return ResponseEntity.ok(savedMetric);
        } catch (Exception e) {
            log.error("Error saving metric: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/metrics/count/{userId}/{metricType}")
    public ResponseEntity<Map<String, Object>> getMetricCount(
            @PathVariable Long userId, 
            @PathVariable String metricType) {
        try {
            log.info("Counting metrics for user: {} and type: {}", userId, metricType);
            Long count = databaseService.getMetricCountByUserIdAndType(userId, metricType);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("metricType", metricType);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error counting metrics: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "dashboard-service");
        health.put("timestamp", new Date());
        return ResponseEntity.ok(health);
    }
} 