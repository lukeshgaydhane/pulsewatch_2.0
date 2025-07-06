package com.pulsewatch.threshold.service;

import com.pulsewatch.threshold.entity.ThresholdConfig;
import com.pulsewatch.threshold.repository.ThresholdConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThresholdConfigService {

    private final ThresholdConfigRepository repository;

    public ThresholdConfigService(ThresholdConfigRepository repository) {
        this.repository = repository;
    }

    public ThresholdConfig saveConfig(ThresholdConfig config) {
        return repository.save(config);
    }

    public Optional<ThresholdConfig> getConfig(Long userId) {
        return repository.findByUserId(userId);
    }
} 