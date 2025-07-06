package com.pulsewatch.threshold.repository;

import com.pulsewatch.threshold.entity.ThresholdConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThresholdConfigRepository extends JpaRepository<ThresholdConfig, Long> {
    Optional<ThresholdConfig> findByUserId(Long userId);
} 