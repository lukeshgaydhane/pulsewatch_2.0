package com.pulsewatch.alerting.service;

import com.pulsewatch.alerting.dto.SendAlertRequest;
import com.pulsewatch.alerting.model.Alert;
import com.pulsewatch.alerting.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;
    private final AlertRepository alertRepository;

    @Value("${alerting.gupshup.api-key}")
    private String gupshupApiKey;

    @Value("${alerting.gupshup.api-url}")
    private String gupshupApiUrl;

    @Value("${alerting.gupshup.source-number}")
    private String gupshupSourceNumber;

    @Value("${alerting.gupshup.app-name}")
    private String gupshupAppName;

    public void sendAlert(SendAlertRequest request) {
        log.info("Sending alert: type={}, recipient={}", request.getType(), request.getRecipient());
        
        boolean success = false;
        String errorMessage = null;

        try {
            switch (request.getType().toUpperCase()) {
                case "EMAIL" -> {
                    sendEmail(request.getRecipient(), "PulseWatch Alert", request.getMessage());
                    success = true;
                    log.info("Email alert sent successfully to: {}", request.getRecipient());
                }
                case "WHATSAPP" -> {
                    sendWhatsApp(request.getRecipient(), request.getMessage());
                    success = true;
                    log.info("WhatsApp alert sent successfully to: {}", request.getRecipient());
                }
                case "IN_APP" -> {
                    log.info("In-app alert: {}", request.getMessage());
                    success = true;
                }
                default -> {
                    errorMessage = "Unsupported alert type: " + request.getType();
                    log.error(errorMessage);
                }
            }
        } catch (Exception e) {
            errorMessage = "Failed to send alert: " + e.getMessage();
            log.error(errorMessage, e);
        }

        // Save alert record
        Alert alert = new Alert();
        alert.setId(UUID.randomUUID());
        alert.setType(request.getType());
        alert.setMessage(request.getMessage());
        alert.setRecipient(request.getRecipient());
        alert.setSentAt(Instant.now());
        alert.setSuccess(success);
        alert.setErrorMessage(errorMessage);

        alertRepository.save(alert);
        log.info("Alert record saved with ID: {}", alert.getId());
    }

    private void sendEmail(String to, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        mailSender.send(mail);
    }

    private void sendWhatsApp(String phoneNumber, String message) {
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("apikey", gupshupApiKey);

            // Prepare form data
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("channel", "whatsapp");
            formData.add("source", gupshupSourceNumber);
            formData.add("destination", phoneNumber);
            formData.add("message", message);
            formData.add("src.name", gupshupAppName);

            // Create HTTP entity
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            // Send request
            restTemplate.postForEntity(gupshupApiUrl, entity, String.class);
            
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}: {}", phoneNumber, e.getMessage());
            throw new RuntimeException("WhatsApp sending failed", e);
        }
    }

    public Alert sendAlertDirectly(SendAlertRequest request) {
        sendAlert(request);
        return alertRepository.findByRecipientOrderBySentAtDesc(request.getRecipient())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alert not found after sending"));
    }

    // Repository query methods
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<Alert> getAlertsByType(String type) {
        return alertRepository.findByTypeOrderBySentAtDesc(type);
    }

    public List<Alert> getAlertsByTimeRange(Instant startTime, Instant endTime) {
        return alertRepository.findBySentAtBetweenOrderBySentAtDesc(startTime, endTime);
    }

    public List<Alert> getAlertsByTypeAndTimeRange(String type, Instant startTime, Instant endTime) {
        return alertRepository.findByTypeAndSentAtBetweenOrderBySentAtDesc(type, startTime, endTime);
    }

    public List<Alert> getAlertsByRecipient(String recipient) {
        return alertRepository.findByRecipientOrderBySentAtDesc(recipient);
    }

    public Alert getAlertById(String alertId) {
        return alertRepository.findById(UUID.fromString(alertId))
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));
    }

    public Object getAlertStats(Instant startTime, Instant endTime) {
        if (startTime == null) {
            startTime = Instant.now().minusSeconds(24 * 60 * 60); // Last 24 hours
        }
        if (endTime == null) {
            endTime = Instant.now();
        }

        long totalAlerts = alertRepository.countBySentAtBetween(startTime, endTime);
        long successfulEmails = alertRepository.countByTypeAndSuccess("EMAIL", true);
        long successfulWhatsApps = alertRepository.countByTypeAndSuccess("WHATSAPP", true);
        long successfulInApps = alertRepository.countByTypeAndSuccess("IN_APP", true);

        return Map.of(
            "totalAlerts", totalAlerts,
            "successfulEmails", successfulEmails,
            "successfulWhatsApps", successfulWhatsApps,
            "successfulInApps", successfulInApps,
            "startTime", startTime,
            "endTime", endTime
        );
    }
} 