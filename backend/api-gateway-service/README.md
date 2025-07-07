# API Gateway Service

A Spring Cloud Gateway microservice that acts as the central entry point for all PulseWatch platform APIs, providing routing, authentication, and request logging.

## 🚀 Overview

The API Gateway Service provides:
- **Centralized Routing** - Routes requests to appropriate microservices
- **Basic Authentication** - HTTP Basic Auth for API security
- **Request Logging** - Comprehensive logging of all incoming requests
- **Response Headers** - Adds tracking headers to responses
- **Health Monitoring** - Actuator endpoints for monitoring

## 📋 Prerequisites

- Java 17 or later
- Maven 3.6+
- Other PulseWatch services running on their respective ports

## 🛠️ Setup & Installation

### 1. Build the Project
```bash
cd backend/api-gateway-service
mvn clean install
```

### 2. Run the Application
```bash
mvn spring-boot:run
```

The service will start on **port 8080**.

## 🔧 Configuration

### Application Properties
The service is configured via `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway-service

  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://localhost:8081
          predicates:
            - Path=/auth/**, /users/**
        
        - id: monitoring-service
          uri: http://localhost:8082
          predicates:
            - Path=/metrics/**

        - id: uptime-service
          uri: http://localhost:8083
          predicates:
            - Path=/uptime/**

        - id: alerting-service
          uri: http://localhost:8084
          predicates:
            - Path=/alerts/**
```

### Environment Variables
You can override default settings using environment variables:

```bash
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=prod
```

## 🔐 Authentication

The gateway uses HTTP Basic Authentication with two default users:

### Default Users
- **Admin User:**
  - Username: `admin`
  - Password: `admin123`
  - Role: `ADMIN`

- **API User:**
  - Username: `api`
  - Password: `api123`
  - Role: `API_USER`

### Public Endpoints
The following endpoints are accessible without authentication:
- `/actuator/**` - Monitoring endpoints
- `/health` - Health check

## 🛣️ Routing Configuration

### Route Mappings

| Path Pattern | Target Service | Port | Description |
|--------------|----------------|------|-------------|
| `/auth/**` | User Service | 8081 | Authentication & user management |
| `/users/**` | User Service | 8081 | User operations |
| `/metrics/**` | Monitoring Service | 8082 | System metrics |
| `/uptime/**` | Uptime Service | 8083 | Uptime monitoring |
| `/alerts/**` | Alerting Service | 8084 | Alert management |

### Request Headers Added
- `X-Gateway-Timestamp` - Request timestamp
- `X-Gateway-ID` - Unique request ID
- `X-Gateway-Service` - Target service name

### Response Headers Added
- `X-Gateway-Response-Time` - Response timestamp
- `X-Gateway-Status` - HTTP status code
- `X-Gateway-Service` - Service that handled the request

## 📊 Monitoring & Health Checks

### Actuator Endpoints
- **Health:** `http://localhost:8080/actuator/health`
- **Info:** `http://localhost:8080/actuator/info`
- **Metrics:** `http://localhost:8080/actuator/metrics`
- **Prometheus:** `http://localhost:8080/actuator/prometheus`
- **Gateway:** `http://localhost:8080/actuator/gateway`

### Health Check
```bash
curl -X GET "http://localhost:8080/health"
```

## 🧪 Testing

### Manual Testing with curl

1. **Test Authentication (Unauthorized):**
```bash
curl -X GET "http://localhost:8080/auth/users"
# Should return 401 Unauthorized
```

2. **Test Authentication (Authorized):**
```bash
curl -X GET "http://localhost:8080/auth/users" \
  -u admin:admin123
# Should return 200 OK (if user service is running)
```

3. **Test Route Forwarding:**
```bash
# Test user service route
curl -X GET "http://localhost:8080/auth/health" \
  -u admin:admin123

# Test metrics route
curl -X GET "http://localhost:8080/metrics/health" \
  -u admin:admin123

# Test uptime route
curl -X GET "http://localhost:8080/uptime/health" \
  -u admin:admin123

# Test alerts route
curl -X GET "http://localhost:8080/alerts/health" \
  -u admin:admin123
```

4. **Test Actuator Endpoints:**
```bash
# Health check
curl -X GET "http://localhost:8080/actuator/health"

# Gateway routes
curl -X GET "http://localhost:8080/actuator/gateway/routes"
```

### Automated Testing
```bash
mvn test
```

## 📝 Logging

### Log Levels
- `org.springframework.cloud.gateway: DEBUG` - Gateway routing logs
- `reactor.netty.http.client: DEBUG` - HTTP client logs
- `com.pulsewatch.gateway: DEBUG` - Custom gateway logs

### Log Format
```
2024-01-15 10:30:00 - Incoming Request: [GET] /auth/users from 127.0.0.1 at 2024-01-15T10:30:00
2024-01-15 10:30:00 - Request completed: [GET] /auth/users - Signal: onComplete
```

## 🔄 Circuit Breaker (Future Enhancement)

Consider adding circuit breaker pattern using:
- Spring Cloud Circuit Breaker
- Resilience4j
- Hystrix (legacy)

## 🐳 Docker Support

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/api-gateway-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  api-gateway:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - user-service
      - monitoring-service
      - uptime-service
      - alerting-service
```

## 🔧 Development

### Project Structure
```
src/main/java/com/pulsewatch/gateway/
├── ApiGatewayApplication.java
├── config/
│   └── SecurityConfig.java
└── filter/
    ├── RequestLoggingFilter.java
    └── ResponseHeaderFilter.java
```

### Adding New Routes

1. Add route configuration in `application.yml`:
```yaml
- id: new-service
  uri: http://localhost:8085
  predicates:
    - Path=/new-service/**
  filters:
    - StripPrefix=0
    - AddRequestHeader=X-Gateway-Service, new-service
```

2. Update documentation

### Custom Filters

To add custom filters:
1. Create a new filter class implementing `GlobalFilter` and `Ordered`
2. Add `@Component` annotation
3. Implement the filter logic in the `filter` method

## 🤝 Contributing

1. Follow the existing code structure
2. Add comprehensive logging
3. Include error handling
4. Write unit tests
5. Update documentation

## 📝 License

This project is part of the PulseWatch monitoring platform.

## 🆘 Troubleshooting

### Common Issues

1. **Service Connection Errors**
   - Verify all target services are running
   - Check service URLs in configuration
   - Ensure network connectivity

2. **Authentication Issues**
   - Verify username/password
   - Check security configuration
   - Ensure proper HTTP Basic Auth headers

3. **Route Not Found**
   - Check route configuration in `application.yml`
   - Verify path predicates
   - Check target service availability

4. **Port Conflicts**
   - Change `server.port` in `application.yml`
   - Ensure port 8080 is available

### Debug Mode
Enable debug logging by adding to `application.yml`:
```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty.http.client: DEBUG
    com.pulsewatch.gateway: DEBUG
```

### Health Check
```bash
curl -X GET "http://localhost:8080/actuator/health"
```

## 📚 References

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Spring Security WebFlux](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/basic.html)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) 