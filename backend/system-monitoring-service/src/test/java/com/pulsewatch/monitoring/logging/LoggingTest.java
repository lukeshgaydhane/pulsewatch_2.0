package com.pulsewatch.monitoring.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LoggingTest {

    private static final Logger log = LoggerFactory.getLogger(LoggingTest.class);

    @Test
    void testLoggingIsWorking() {
        // Test that logger is properly initialized
        assertNotNull(log);
        
        // Test different log levels
        log.trace("This is a TRACE message");
        log.debug("This is a DEBUG message");
        log.info("This is an INFO message");
        log.warn("This is a WARN message");
        log.error("This is an ERROR message");
        
        // Verify logger name
        assertNotNull(log.getName());
        System.out.println("Logger name: " + log.getName());
    }

    @Test
    void testLoggerFactory() {
        // Test creating logger for different classes
        Logger controllerLogger = LoggerFactory.getLogger("com.pulsewatch.monitoring.controller.MetricsController");
        Logger serviceLogger = LoggerFactory.getLogger("com.pulsewatch.monitoring.service.impl.MetricsServiceImpl");
        
        assertNotNull(controllerLogger);
        assertNotNull(serviceLogger);
        
        controllerLogger.info("Controller logger test");
        serviceLogger.info("Service logger test");
    }
} 