package com.pulsewatch.dashboard.service;

import com.pulsewatch.dashboard.entity.DashboardMetrics;

import java.util.List;
import java.util.Map;

public interface DatabaseService {
    
    // JPA Repository methods
    DashboardMetrics saveMetric(DashboardMetrics metric);
    
    List<DashboardMetrics> getMetricsByUserId(Long userId);
    
    List<DashboardMetrics> getMetricsByUserIdAndType(Long userId, String metricType);
    
    Long getMetricCountByUserIdAndType(Long userId, String metricType);
    
    // JdbcTemplate methods for custom queries
    List<Map<String, Object>> executeCustomQuery(String query);
    
    List<Map<String, Object>> getDashboardSummaryFromDB(Long userId);
    
    void executeUpdate(String sql, Object... params);
} 