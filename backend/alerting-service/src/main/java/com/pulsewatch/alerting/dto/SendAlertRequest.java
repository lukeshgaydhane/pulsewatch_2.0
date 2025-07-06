package com.pulsewatch.alerting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendAlertRequest {
    
    @NotBlank(message = "Alert type is required")
    private String type; // "EMAIL", "WHATSAPP", "IN_APP"
    
    @NotBlank(message = "Alert message is required")
    private String message;
    
    @NotBlank(message = "Recipient is required")
    private String recipient; // Email or phone number
} 