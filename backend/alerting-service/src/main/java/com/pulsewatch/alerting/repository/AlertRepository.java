package com.pulsewatch.alerting.repository;

import com.pulsewatch.alerting.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    
    List<Alert> findByTypeOrderBySentAtDesc(String type);
    
    List<Alert> findBySuccessOrderBySentAtDesc(boolean success);
    
    @Query("SELECT a FROM Alert a WHERE a.sentAt BETWEEN :startTime AND :endTime ORDER BY a.sentAt DESC")
    List<Alert> findBySentAtBetweenOrderBySentAtDesc(@Param("startTime") Instant startTime, 
                                                    @Param("endTime") Instant endTime);
    
    @Query("SELECT a FROM Alert a WHERE a.type = :type AND a.sentAt BETWEEN :startTime AND :endTime ORDER BY a.sentAt DESC")
    List<Alert> findByTypeAndSentAtBetweenOrderBySentAtDesc(@Param("type") String type,
                                                           @Param("startTime") Instant startTime,
                                                           @Param("endTime") Instant endTime);
    
    @Query("SELECT a FROM Alert a WHERE a.recipient = :recipient ORDER BY a.sentAt DESC")
    List<Alert> findByRecipientOrderBySentAtDesc(@Param("recipient") String recipient);
    
    long countByTypeAndSuccess(String type, boolean success);
    
    long countBySentAtBetween(Instant startTime, Instant endTime);
} 