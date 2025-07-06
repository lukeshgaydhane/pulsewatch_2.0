package com.pulsewatch.uptime.controller;

import com.pulsewatch.uptime.dto.RegisterServiceRequest;
import com.pulsewatch.uptime.dto.ServiceStatusResponse;
import com.pulsewatch.uptime.model.MonitoredService;
import com.pulsewatch.uptime.service.UptimeMonitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/uptime")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UptimeController {

    private static final Logger log = LoggerFactory.getLogger(UptimeController.class);
    
    private final UptimeMonitorService uptimeMonitorService;

    @PostMapping("/register")
    public ResponseEntity<ServiceStatusResponse> registerService(@Valid @RequestBody RegisterServiceRequest request) {
        log.info("Received service registration request: {} at {}", request.getName(), request.getUrl());
        
        try {
            MonitoredService service = uptimeMonitorService.registerService(request.getName(), request.getUrl());
            ServiceStatusResponse response = ServiceStatusResponse.from(service);
            
            log.info("Service registered successfully with ID: {}", service.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid request data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error registering service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{serviceId}")
    public ResponseEntity<ServiceStatusResponse> getServiceStatus(@PathVariable String serviceId) {
        log.info("Requesting status for service ID: {}", serviceId);
        
        try {
            UUID uuid = UUID.fromString(serviceId);
            MonitoredService service = uptimeMonitorService.getServiceStatus(uuid);
            ServiceStatusResponse response = ServiceStatusResponse.from(service);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid service ID format: {}", serviceId);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Service not found with ID: {}", serviceId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving service status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceStatusResponse>> getAllServices() {
        log.info("Requesting all monitored services");
        
        try {
            List<MonitoredService> services = uptimeMonitorService.getAllServices();
            List<ServiceStatusResponse> responses = services.stream()
                    .map(ServiceStatusResponse::from)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            log.error("Error retrieving all services: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable String serviceId) {
        log.info("Requesting deletion of service ID: {}", serviceId);
        
        try {
            UUID uuid = UUID.fromString(serviceId);
            uptimeMonitorService.deleteService(uuid);
            
            log.info("Service deleted successfully: {}", serviceId);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid service ID format: {}", serviceId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error deleting service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/check/{serviceId}")
    public ResponseEntity<ServiceStatusResponse> checkServiceNow(@PathVariable String serviceId) {
        log.info("Manual health check requested for service ID: {}", serviceId);
        
        try {
            UUID uuid = UUID.fromString(serviceId);
            MonitoredService service = uptimeMonitorService.getServiceStatus(uuid);
            
            // Perform immediate health check
            // This would require adding a method to check a single service
            // For now, we'll return the current status
            
            ServiceStatusResponse response = ServiceStatusResponse.from(service);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid service ID format: {}", serviceId);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Service not found with ID: {}", serviceId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error performing manual health check: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 