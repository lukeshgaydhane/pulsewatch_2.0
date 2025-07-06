package com.pulsewatch.monitoring.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;

class MetricsRequestTest {

    @Test
    void testMetricsRequestGettersAndSetters() {
        // Test that Lombok @Data annotation generates getters and setters
        MetricsRequest request = new MetricsRequest();
        
        // Test setters
        request.setHost("test-server");
        request.setTimestamp(LocalDateTime.now());
        
        MetricsRequest.MetricData metricData = new MetricsRequest.MetricData();
        metricData.setMetricType("CPU");
        metricData.setMetricName("cpu_usage");
        metricData.setMetricValue(75.5);
        metricData.setUnit("%");
        
        request.setMetrics(Arrays.asList(metricData));
        
        // Test getters
        assertEquals("test-server", request.getHost());
        assertNotNull(request.getTimestamp());
        assertEquals(1, request.getMetrics().size());
        assertEquals("CPU", request.getMetrics().get(0).getMetricType());
        assertEquals("cpu_usage", request.getMetrics().get(0).getMetricName());
        assertEquals(75.5, request.getMetrics().get(0).getMetricValue());
        assertEquals("%", request.getMetrics().get(0).getUnit());
    }

    @Test
    void testMetricsResponseGettersAndSetters() {
        // Test that Lombok @Data annotation generates getters and setters
        MetricsResponse response = new MetricsResponse();
        
        // Test setters
        response.setHost("test-server");
        response.setMetricType("CPU");
        
        MetricsResponse.MetricPoint point = new MetricsResponse.MetricPoint();
        point.setTimestamp(LocalDateTime.now());
        point.setValue(75.5);
        point.setUnit("%");
        
        response.setData(Arrays.asList(point));
        
        // Test getters
        assertEquals("test-server", response.getHost());
        assertEquals("CPU", response.getMetricType());
        assertEquals(1, response.getData().size());
        assertEquals(75.5, response.getData().get(0).getValue());
        assertEquals("%", response.getData().get(0).getUnit());
    }
} 