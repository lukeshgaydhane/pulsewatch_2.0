package com.pulsewatch.uptime.service;

import com.pulsewatch.uptime.model.MonitoredService;
import com.pulsewatch.uptime.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UptimeMonitorService {

    private static final Logger log = LoggerFactory.getLogger(UptimeMonitorService.class);
    
    private final MonitoredServiceRepository repository;
    private final RestTemplate restTemplate;
    
    @Value("${uptime.monitoring.timeout-ms:5000}")
    private int timeoutMs;
    
    @Value("${uptime.monitoring.user-agent:PulseWatch-UptimeMonitor/1.0}")
    private String userAgent;

    @Scheduled(fixedRateString = "${uptime.monitoring.interval-ms:60000}")
    public void checkAllServices() {
        log.info("Starting scheduled health check for all monitored services");
        
        List<MonitoredService> services = repository.findAll();
        
        if (services.isEmpty()) {
            log.info("No services to monitor");
            return;
        }
        
        log.info("Checking {} services", services.size());
        
        for (MonitoredService service : services) {
            try {
                checkServiceHealth(service);
            } catch (Exception e) {
                log.error("Error checking service {}: {}", service.getName(), e.getMessage());
                markServiceAsDown(service, e.getMessage());
            }
        }
        
        log.info("Completed health check for {} services", services.size());
    }

    private void checkServiceHealth(MonitoredService service) {
        long startTime = System.currentTimeMillis();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", userAgent);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                service.getUrl(),
                HttpMethod.GET,
                entity,
                String.class
            );
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (response.getStatusCode().is2xxSuccessful()) {
                markServiceAsUp(service, responseTime);
                log.debug("Service {} is UP ({}ms)", service.getName(), responseTime);
            } else {
                markServiceAsDown(service, "HTTP " + response.getStatusCode().value());
                log.warn("Service {} is DOWN - HTTP {}", service.getName(), response.getStatusCode().value());
            }
            
        } catch (ResourceAccessException e) {
            long responseTime = System.currentTimeMillis() - startTime;
            markServiceAsDown(service, "Connection timeout: " + e.getMessage());
            log.warn("Service {} is DOWN - Connection timeout", service.getName());
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            markServiceAsDown(service, e.getMessage());
            log.warn("Service {} is DOWN - {}", service.getName(), e.getMessage());
        }
    }

    private void markServiceAsUp(MonitoredService service, long responseTime) {
        service.setStatus(MonitoredService.ServiceStatus.UP);
        service.setLastChecked(Instant.now());
        service.setResponseTimeMs(responseTime);
        service.setErrorMessage(null);
        repository.save(service);
    }

    private void markServiceAsDown(MonitoredService service, String errorMessage) {
        service.setStatus(MonitoredService.ServiceStatus.DOWN);
        service.setLastChecked(Instant.now());
        service.setResponseTimeMs(null);
        service.setErrorMessage(errorMessage);
        repository.save(service);
    }

    public MonitoredService registerService(String name, String url) {
        log.info("Registering new service: {} at {}", name, url);
        
        // Validate URL format in service layer
        validateUrl(url);
        
        MonitoredService service = new MonitoredService();
        service.setName(name);
        service.setUrl(url);
        service.setStatus(MonitoredService.ServiceStatus.UNKNOWN);
        service.setCreatedAt(Instant.now());
        service.setUpdatedAt(Instant.now());
        
        MonitoredService savedService = repository.save(service);
        log.info("Service registered with ID: {}", savedService.getId());
        
        return savedService;
    }
    
    private void validateUrl(String url) {
        try {
            new java.net.URL(url).toURI();
        } catch (Exception e) {
            log.error("Invalid URL format: {}", url);
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
    }

    public MonitoredService getServiceStatus(UUID serviceId) {
        return repository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
    }

    public List<MonitoredService> getAllServices() {
        return repository.findAll();
    }

    public void deleteService(UUID serviceId) {
        log.info("Deleting service with ID: {}", serviceId);
        repository.deleteById(serviceId);
    }
} 