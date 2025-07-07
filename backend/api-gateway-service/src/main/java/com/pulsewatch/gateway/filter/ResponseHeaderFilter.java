package com.pulsewatch.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Component
public class ResponseHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    ServerHttpResponse response = exchange.getResponse();
                    
                    // Add response headers
                    response.getHeaders().add("X-Gateway-Response-Time", 
                        Instant.now().toString());
                    response.getHeaders().add("X-Gateway-Status", 
                        String.valueOf(response.getStatusCode().value()));
                    response.getHeaders().add("X-Gateway-Service", 
                        exchange.getRequest().getPath().value());
                    
                    log.debug("Response headers added for request: {}", 
                        exchange.getRequest().getPath().value());
                });
    }

    @Override
    public int getOrder() {
        return 1; // Lower priority than RequestLoggingFilter
    }
} 