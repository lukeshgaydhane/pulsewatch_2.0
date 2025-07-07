package com.pulsewatch.dashboard.repository;

import com.pulsewatch.dashboard.entity.DashboardMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardMetricsRepository extends JpaRepository<DashboardMetrics, Long> {

    List<DashboardMetrics> findByUserId(Long userId);
    
    List<DashboardMetrics> findByUserIdAndMetricType(Long userId, String metricType);
    
    @Query("SELECT dm FROM DashboardMetrics dm WHERE dm.userId = :userId ORDER BY dm.createdAt DESC")
    List<DashboardMetrics> findLatestMetricsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(dm) FROM DashboardMetrics dm WHERE dm.userId = :userId AND dm.metricType = :metricType")
    Long countByUserIdAndMetricType(@Param("userId") Long userId, @Param("metricType") String metricType);
} 