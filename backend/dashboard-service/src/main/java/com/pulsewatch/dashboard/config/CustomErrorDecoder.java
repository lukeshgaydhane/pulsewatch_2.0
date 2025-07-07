package com.pulsewatch.dashboard.config;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign client error for method: {}, status: {}", methodKey, response.status());
        
        byte[] responseBody = new byte[0];
        try {
            if (response.body() != null) {
                responseBody = response.body().asInputStream().readAllBytes();
            }
        } catch (IOException e) {
            log.warn("Could not read response body: {}", e.getMessage());
        }
        
        switch (response.status()) {
            case 400:
                return new FeignException.BadRequest("Bad Request", response.request(), responseBody, null);
            case 401:
                return new FeignException.Unauthorized("Unauthorized", response.request(), responseBody, null);
            case 403:
                return new FeignException.Forbidden("Forbidden", response.request(), responseBody, null);
            case 404:
                return new FeignException.NotFound("Not Found", response.request(), responseBody, null);
            case 500:
                return new FeignException.InternalServerError("Internal Server Error", response.request(), responseBody, null);
            default:
                return new FeignException.FeignServerException(response.status(), "Service unavailable", response.request(), responseBody, null);
        }
    }
} 