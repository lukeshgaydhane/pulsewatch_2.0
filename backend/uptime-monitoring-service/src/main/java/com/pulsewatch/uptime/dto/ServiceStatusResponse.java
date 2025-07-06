package com.pulsewatch.uptime.dto;

import com.pulsewatch.uptime.model.MonitoredService;
import lombok.Data;

import java.time.Instant;

@Data
public class ServiceStatusResponse {
    
    private String id;
    private String name;
    private String url;
    private String status;
    private Instant lastChecked;
    private Long responseTimeMs;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;
    
    public static ServiceStatusResponse from(MonitoredService service) {
        ServiceStatusResponse response = new ServiceStatusResponse();
        response.setId(service.getId().toString());
        response.setName(service.getName());
        response.setUrl(service.getUrl());
        response.setStatus(service.getStatus().name());
        response.setLastChecked(service.getLastChecked());
        response.setResponseTimeMs(service.getResponseTimeMs());
        response.setErrorMessage(service.getErrorMessage());
        response.setCreatedAt(service.getCreatedAt());
        response.setUpdatedAt(service.getUpdatedAt());
        return response;
    }
} 