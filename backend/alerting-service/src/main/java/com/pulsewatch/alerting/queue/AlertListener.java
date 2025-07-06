package com.pulsewatch.alerting.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsewatch.alerting.dto.SendAlertRequest;
import com.pulsewatch.alerting.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertListener {

    private static final Logger log = LoggerFactory.getLogger(AlertListener.class);
    
    private final AlertService alertService;
    private final ObjectMapper objectMapper;

    @Value("${alerting.rabbitmq.queue.name:alerts.queue}")
    private String queueName;

    @RabbitListener(queues = "${alerting.rabbitmq.queue.name:alerts.queue}")
    public void receiveAlert(String message) {
        log.info("Received alert message from queue: {}", message);
        
        try {
            SendAlertRequest request = objectMapper.readValue(message, SendAlertRequest.class);
            alertService.sendAlert(request);
            log.info("Alert processed successfully for type: {}", request.getType());
        } catch (Exception e) {
            log.error("Failed to process alert message: {}", message, e);
        }
    }
} 