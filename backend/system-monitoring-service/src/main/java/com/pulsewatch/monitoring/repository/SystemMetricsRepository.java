package com.pulsewatch.monitoring.repository;

import com.pulsewatch.monitoring.model.SystemMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemMetricsRepository extends JpaRepository<SystemMetrics, Long> {
    
    @Query("SELECT sm FROM SystemMetrics sm WHERE sm.metricType = :metricType AND sm.timestamp >= :startTime ORDER BY sm.timestamp DESC")
    List<SystemMetrics> findByMetricTypeAndTimestampAfter(@Param("metricType") SystemMetrics.MetricType metricType, 
                                                         @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT sm FROM SystemMetrics sm WHERE sm.host = :host AND sm.metricType = :metricType AND sm.timestamp >= :startTime ORDER BY sm.timestamp DESC")
    List<SystemMetrics> findByHostAndMetricTypeAndTimestampAfter(@Param("host") String host,
                                                                @Param("metricType") SystemMetrics.MetricType metricType,
                                                                @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT sm FROM SystemMetrics sm WHERE sm.metricType = :metricType AND sm.timestamp >= :startTime AND sm.timestamp <= :endTime ORDER BY sm.timestamp ASC")
    List<SystemMetrics> findByMetricTypeAndTimestampBetween(@Param("metricType") SystemMetrics.MetricType metricType,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT AVG(sm.metricValue) FROM SystemMetrics sm WHERE sm.metricType = :metricType AND sm.timestamp >= :startTime")
    Double getAverageMetricValue(@Param("metricType") SystemMetrics.MetricType metricType,
                                @Param("startTime") LocalDateTime startTime);
} 