package com.pulsewatch.dashboard.service.impl;

import com.pulsewatch.dashboard.client.AlertClient;
import com.pulsewatch.dashboard.client.PredictionClient;
import com.pulsewatch.dashboard.client.ConfigClient;
import com.pulsewatch.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final AlertClient alertClient;
    private final PredictionClient predictionClient;
    private final ConfigClient configClient;

    @Override
    public Map<String, Object> getDashboardSummary(Long userId) {
        log.info("Aggregating dashboard summary for user: {}", userId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("timestamp", new Date());
        summary.put("userId", userId);
        
        try {
            // Fetch alerts
            List<Map<String, Object>> alerts = alertClient.getLatestAlerts();
            summary.put("alerts", alerts);
            summary.put("alertCount", alerts.size());
            
            // Fetch predictions
            Map<String, Object> predictions = predictionClient.getPredictionSummary();
            summary.put("predictions", predictions);
            
            // Fetch user configuration
            Map<String, Object> config = configClient.getConfigByUserId(userId);
            summary.put("config", config);
            
            // Add summary statistics
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalAlerts", alerts.size());
            stats.put("hasPredictions", predictions != null && !predictions.isEmpty());
            stats.put("hasConfig", config != null && !config.isEmpty());
            summary.put("statistics", stats);
            
            log.info("Successfully aggregated dashboard summary for user: {}", userId);
            return summary;
            
        } catch (Exception e) {
            log.error("Error aggregating dashboard summary for user {}: {}", userId, e.getMessage());
            summary.put("error", "Failed to aggregate dashboard data");
            summary.put("errorMessage", e.getMessage());
            return summary;
        }
    }

    @Override
    public List<Map<String, Object>> getAlerts(Integer limit) {
        log.info("Fetching alerts with limit: {}", limit);
        try {
            return alertClient.getAllAlerts(limit);
        } catch (Exception e) {
            log.error("Error fetching alerts: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getPredictions() {
        log.info("Fetching prediction summary");
        try {
            return predictionClient.getPredictionSummary();
        } catch (Exception e) {
            log.error("Error fetching predictions: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch predictions");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public Map<String, Object> getAnomalies() {
        log.info("Fetching anomaly summary");
        try {
            return predictionClient.getAnomalySummary();
        } catch (Exception e) {
            log.error("Error fetching anomalies: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch anomalies");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public Map<String, Object> getUserConfig(Long userId) {
        log.info("Fetching config for user: {}", userId);
        try {
            return configClient.getConfigByUserId(userId);
        } catch (Exception e) {
            log.error("Error fetching config for user {}: {}", userId, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch user config");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }
} 