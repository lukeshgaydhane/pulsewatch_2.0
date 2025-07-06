package com.pulsewatch.uptime.repository;

import com.pulsewatch.uptime.model.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, UUID> {
    
    Optional<MonitoredService> findByName(String name);
    
    List<MonitoredService> findByStatus(MonitoredService.ServiceStatus status);
    
    @Query("SELECT ms FROM MonitoredService ms WHERE ms.lastChecked < :threshold")
    List<MonitoredService> findServicesNotCheckedSince(@Param("threshold") Instant threshold);
    
    @Query("SELECT COUNT(ms) FROM MonitoredService ms WHERE ms.status = :status")
    long countByStatus(@Param("status") MonitoredService.ServiceStatus status);
    
    @Query("SELECT ms FROM MonitoredService ms ORDER BY ms.lastChecked ASC")
    List<MonitoredService> findAllOrderByLastChecked();
} 