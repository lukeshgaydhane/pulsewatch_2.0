package com.pulsewatch.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();
        String path = request.getPath().value();
        String remoteAddress = request.getRemoteAddress() != null ? 
            request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        
        LocalDateTime timestamp = LocalDateTime.now(ZoneId.systemDefault());
        
        log.info("Incoming Request: [{}] {} from {} at {}", 
                method, path, remoteAddress, timestamp);
        
        // Log request headers for debugging
        if (log.isDebugEnabled()) {
            request.getHeaders().forEach((key, value) -> 
                log.debug("Header: {} = {}", key, value));
        }
        
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    log.info("Request completed: [{}] {} - Signal: {}", 
                            method, path, signalType);
                });
    }

    @Override
    public int getOrder() {
        return -1; // High priority to log before other filters
    }
} 