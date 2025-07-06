package com.pulsewatch.threshold.controller;

import com.pulsewatch.threshold.entity.ThresholdConfig;
import com.pulsewatch.threshold.service.ThresholdConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ThresholdConfigController {

    private final ThresholdConfigService service;

    public ThresholdConfigController(ThresholdConfigService service) {
        this.service = service;
    }

    @PostMapping("/threshold")
    public ResponseEntity<ThresholdConfig> saveConfig(@RequestBody ThresholdConfig config) {
        return ResponseEntity.ok(service.saveConfig(config));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ThresholdConfig> getConfig(@PathVariable Long userId) {
        return service.getConfig(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 