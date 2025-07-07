package com.pulsewatch.dashboard.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    
    Map<String, Object> getDashboardSummary(Long userId);
    
    List<Map<String, Object>> getAlerts(Integer limit);
    
    Map<String, Object> getPredictions();
    
    Map<String, Object> getAnomalies();
    
    Map<String, Object> getUserConfig(Long userId);
} 