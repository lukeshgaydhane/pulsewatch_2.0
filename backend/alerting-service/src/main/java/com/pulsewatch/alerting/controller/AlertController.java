package com.pulsewatch.alerting.controller;

import com.pulsewatch.alerting.dto.SendAlertRequest;
import com.pulsewatch.alerting.model.Alert;
import com.pulsewatch.alerting.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);
    
    private final AlertService alertService;

    @PostMapping("/send")
    public ResponseEntity<Alert> sendAlert(@Valid @RequestBody SendAlertRequest request) {
        log.info("Received alert request: type={}, recipient={}", request.getType(), request.getRecipient());
        
        try {
            Alert alert = alertService.sendAlertDirectly(request);
            log.info("Alert sent successfully with ID: {}", alert.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(alert);
        } catch (Exception e) {
            log.error("Failed to send alert: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Alert>> getAlertHistory(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @RequestParam(required = false) String recipient) {
        
        log.info("Retrieving alert history: type={}, startTime={}, endTime={}, recipient={}", 
                type, startTime, endTime, recipient);
        
        try {
            List<Alert> alerts;
            
            if (recipient != null) {
                alerts = alertService.getAlertsByRecipient(recipient);
            } else if (type != null && startTime != null && endTime != null) {
                alerts = alertService.getAlertsByTypeAndTimeRange(type, startTime, endTime);
            } else if (type != null) {
                alerts = alertService.getAlertsByType(type);
            } else if (startTime != null && endTime != null) {
                alerts = alertService.getAlertsByTimeRange(startTime, endTime);
            } else {
                alerts = alertService.getAllAlerts();
            }
            
            log.info("Retrieved {} alerts", alerts.size());
            return ResponseEntity.ok(alerts);
            
        } catch (Exception e) {
            log.error("Failed to retrieve alert history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history/{alertId}")
    public ResponseEntity<Alert> getAlertById(@PathVariable String alertId) {
        log.info("Retrieving alert by ID: {}", alertId);
        
        try {
            Alert alert = alertService.getAlertById(alertId);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            log.error("Alert not found with ID: {}", alertId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getAlertStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        log.info("Retrieving alert statistics: startTime={}, endTime={}", startTime, endTime);
        
        try {
            Object stats = alertService.getAlertStats(startTime, endTime);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to retrieve alert statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 