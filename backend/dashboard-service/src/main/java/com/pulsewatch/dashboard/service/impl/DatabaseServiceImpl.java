package com.pulsewatch.dashboard.service.impl;

import com.pulsewatch.dashboard.entity.DashboardMetrics;
import com.pulsewatch.dashboard.repository.DashboardMetricsRepository;
import com.pulsewatch.dashboard.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseServiceImpl implements DatabaseService {

    private final DashboardMetricsRepository metricsRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public DashboardMetrics saveMetric(DashboardMetrics metric) {
        log.info("Saving metric for user: {}, type: {}", metric.getUserId(), metric.getMetricType());
        return metricsRepository.save(metric);
    }

    @Override
    public List<DashboardMetrics> getMetricsByUserId(Long userId) {
        log.info("Fetching metrics for user: {}", userId);
        return metricsRepository.findByUserId(userId);
    }

    @Override
    public List<DashboardMetrics> getMetricsByUserIdAndType(Long userId, String metricType) {
        log.info("Fetching metrics for user: {} and type: {}", userId, metricType);
        return metricsRepository.findByUserIdAndMetricType(userId, metricType);
    }

    @Override
    public Long getMetricCountByUserIdAndType(Long userId, String metricType) {
        log.info("Counting metrics for user: {} and type: {}", userId, metricType);
        return metricsRepository.countByUserIdAndMetricType(userId, metricType);
    }

    @Override
    public List<Map<String, Object>> executeCustomQuery(String query) {
        log.info("Executing custom query: {}", query);
        try {
            return jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            log.error("Error executing custom query: {}", e.getMessage());
            throw new RuntimeException("Failed to execute custom query", e);
        }
    }

    @Override
    public List<Map<String, Object>> getDashboardSummaryFromDB(Long userId) {
        log.info("Fetching dashboard summary from database for user: {}", userId);
        String query = """
            SELECT 
                dm.metric_type,
                dm.metric_value,
                dm.created_at,
                dm.updated_at
            FROM dashboard_metrics dm
            WHERE dm.user_id = ?
            ORDER BY dm.created_at DESC
            LIMIT 10
            """;
        
        try {
            return jdbcTemplate.queryForList(query, userId);
        } catch (Exception e) {
            log.error("Error fetching dashboard summary from database: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch dashboard summary", e);
        }
    }

    @Override
    public void executeUpdate(String sql, Object... params) {
        log.info("Executing update query: {}", sql);
        try {
            jdbcTemplate.update(sql, params);
        } catch (Exception e) {
            log.error("Error executing update query: {}", e.getMessage());
            throw new RuntimeException("Failed to execute update query", e);
        }
    }
} 