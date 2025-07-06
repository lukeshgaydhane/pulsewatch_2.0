package com.pulsewatch.threshold.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThresholdConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Integer cpuThreshold;    // e.g. 85
    private Integer ramThreshold;    // e.g. 80

    private boolean emailAlert;
    private boolean smsAlert;
    private boolean pushAlert;
} 