package com.pulsewatch.dashboard.service;

import com.pulsewatch.dashboard.entity.DashboardMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseServiceTest {

    @Autowired
    private DatabaseService databaseService;

    @Test
    void testSaveAndRetrieveMetric() {
        // Create a test metric
        DashboardMetrics metric = new DashboardMetrics();
        metric.setUserId(1L);
        metric.setMetricType("ALERT_COUNT");
        metric.setMetricValue("5");

        // Save the metric
        DashboardMetrics savedMetric = databaseService.saveMetric(metric);
        
        // Verify the metric was saved
        assertNotNull(savedMetric.getId());
        assertEquals(1L, savedMetric.getUserId());
        assertEquals("ALERT_COUNT", savedMetric.getMetricType());
        assertEquals("5", savedMetric.getMetricValue());

        // Retrieve metrics for the user
        List<DashboardMetrics> userMetrics = databaseService.getMetricsByUserId(1L);
        assertFalse(userMetrics.isEmpty());
        assertTrue(userMetrics.stream().anyMatch(m -> m.getMetricType().equals("ALERT_COUNT")));
    }

    @Test
    void testGetMetricsByType() {
        // Create test metrics
        DashboardMetrics metric1 = new DashboardMetrics();
        metric1.setUserId(1L);
        metric1.setMetricType("ALERT_COUNT");
        metric1.setMetricValue("5");
        databaseService.saveMetric(metric1);

        DashboardMetrics metric2 = new DashboardMetrics();
        metric2.setUserId(1L);
        metric2.setMetricType("PREDICTION_COUNT");
        metric2.setMetricValue("3");
        databaseService.saveMetric(metric2);

        // Get metrics by type
        List<DashboardMetrics> alertMetrics = databaseService.getMetricsByUserIdAndType(1L, "ALERT_COUNT");
        assertEquals(1, alertMetrics.size());
        assertEquals("ALERT_COUNT", alertMetrics.get(0).getMetricType());
    }

    @Test
    void testGetMetricCount() {
        // Create test metrics
        DashboardMetrics metric1 = new DashboardMetrics();
        metric1.setUserId(1L);
        metric1.setMetricType("ALERT_COUNT");
        metric1.setMetricValue("5");
        databaseService.saveMetric(metric1);

        DashboardMetrics metric2 = new DashboardMetrics();
        metric2.setUserId(1L);
        metric2.setMetricType("ALERT_COUNT");
        metric2.setMetricValue("3");
        databaseService.saveMetric(metric2);

        // Get count
        Long count = databaseService.getMetricCountByUserIdAndType(1L, "ALERT_COUNT");
        assertEquals(2L, count);
    }

    @Test
    void testCustomQuery() {
        // Create a test metric
        DashboardMetrics metric = new DashboardMetrics();
        metric.setUserId(1L);
        metric.setMetricType("TEST_METRIC");
        metric.setMetricValue("test_value");
        databaseService.saveMetric(metric);

        // Execute custom query
        List<Map<String, Object>> results = databaseService.executeCustomQuery(
            "SELECT * FROM dashboard_metrics WHERE user_id = 1"
        );
        
        assertFalse(results.isEmpty());
    }
} 