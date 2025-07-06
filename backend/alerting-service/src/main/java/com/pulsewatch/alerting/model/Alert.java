package com.pulsewatch.alerting.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String type; // "EMAIL", "WHATSAPP", "IN_APP"
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Column(nullable = false)
    private String recipient;
    
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;
    
    @Column(nullable = false)
    private boolean success;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (sentAt == null) {
            sentAt = Instant.now();
        }
    }
} 