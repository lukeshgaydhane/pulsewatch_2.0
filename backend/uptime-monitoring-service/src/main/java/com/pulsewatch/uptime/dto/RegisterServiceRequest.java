package com.pulsewatch.uptime.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterServiceRequest {
    
    @NotBlank(message = "Service name is required")
    private String name;
    
    @NotBlank(message = "Service URL is required")
    private String url;  // Treat as plain string - validate in service layer if needed
} 