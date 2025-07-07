# Dashboard Service

A Spring Boot microservice that aggregates data from multiple PulseWatch services to provide a unified dashboard API for the frontend.

## 🚀 Overview

The Dashboard Service acts as an aggregator that collects and combines data from:
- **Alerting Service** (port 8081) - Latest alerts and notifications
- **AI Smart Alerting Service** (port 8082) - Predictions and anomaly detection
- **Threshold Service** (port 8085) - User configurations and thresholds
- **PostgreSQL Database** (port 5432) - Local metrics and dashboard data storage

## 📋 Prerequisites

- Java 17 or later
- Maven 3.6+
- PostgreSQL database running on port 5432
- Other PulseWatch services running:
  - alerting-service (port 8081)
  - ai-smart-alerting (port 8082)
  - threshold-service (port 8085)

## 🛠️ Setup & Installation

### 1. Database Setup

Create the PostgreSQL database:
```sql
CREATE DATABASE pulsewatch;
CREATE USER postgres WITH PASSWORD 'postgresql';
GRANT ALL PRIVILEGES ON DATABASE pulsewatch TO postgres;
```

### 2. Clone and Navigate
```bash
cd backend/dashboard-service
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The service will start on **port 8086**.

## 🔧 Configuration

### Application Properties
The service is configured via `application.yml`:

```yaml
server:
  port: 8086

spring:
  application:
    name: dashboard-service
  
  # Database Configuration
  datasource:
    url: jdbc:postgresql://localhost:5432/pulsewatch
    username: postgres
    password: postgresql
    driver-class-name: org.postgresql.Driver
  
  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: basic
```

### Environment Variables
You can override default settings using environment variables:

```bash
export SERVER_PORT=8086
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pulsewatch
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgresql
export ALERT_SERVICE_URL=http://localhost:8081
export PREDICTION_SERVICE_URL=http://localhost:8082
export CONFIG_SERVICE_URL=http://localhost:8085
```

## 📡 API Endpoints

### 1. Dashboard Summary
**GET** `/dashboard/summary?userId={userId}`

Returns aggregated data from all services for a specific user.

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "userId": 1,
  "alerts": [...],
  "predictions": {...},
  "config": {...},
  "databaseMetrics": [...],
  "statistics": {
    "totalAlerts": 5,
    "hasPredictions": true,
    "hasConfig": true
  }
}
```

### 2. Alerts
**GET** `/dashboard/alerts?limit={limit}`

Returns latest alerts from the alerting service.

**Parameters:**
- `limit` (optional): Number of alerts to return (default: 10)

### 3. Predictions
**GET** `/dashboard/predictions`

Returns prediction summary from the AI service.

### 4. Anomalies
**GET** `/dashboard/anomalies`

Returns anomaly detection summary.

### 5. User Configuration
**GET** `/dashboard/config/{userId}`

Returns user-specific configuration and thresholds.

### 6. Database Endpoints

#### Save Metric
**POST** `/dashboard/metrics`

Save a new dashboard metric to the database.

**Request Body:**
```json
{
  "userId": 1,
  "metricType": "ALERT_COUNT",
  "metricValue": "5"
}
```

#### Get User Metrics
**GET** `/dashboard/metrics/{userId}`

Returns all metrics for a specific user.

#### Get Metrics by Type
**GET** `/dashboard/metrics/{userId}/{metricType}`

Returns metrics for a specific user and metric type.

#### Get Metric Count
**GET** `/dashboard/metrics/count/{userId}/{metricType}`

Returns the count of metrics for a specific user and metric type.

### 7. Health Check
**GET** `/dashboard/health`

Returns service health status.

## 🔌 Service Communication

The service uses **Spring Cloud OpenFeign** for REST-based service-to-service communication:

### Feign Clients

1. **AlertClient** - Communicates with alerting-service
   - URL: `http://localhost:8081`
   - Endpoints: `/alerts/latest`, `/alerts`, `/alerts/count`

2. **PredictionClient** - Communicates with ai-smart-alerting
   - URL: `http://localhost:8082`
   - Endpoints: `/ai/predict`, `/ai/anomalies`

3. **ConfigClient** - Communicates with threshold-service
   - URL: `http://localhost:8085`
   - Endpoints: `/config/{userId}`, `/thresholds`

## 🗄️ Database Schema

The service automatically creates the following table:

```sql
CREATE TABLE dashboard_metrics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    metric_type VARCHAR(255) NOT NULL,
    metric_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🧪 Testing

### Manual Testing with PowerShell

1. **Get Dashboard Summary:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8086/dashboard/summary?userId=1" -Method GET
```

2. **Save Metric:**
```powershell
$metricData = @{
    userId = 1
    metricType = "TEST_METRIC"
    metricValue = "test_value"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8086/dashboard/metrics" -Method POST -Body $metricData -ContentType "application/json"
```

3. **Get User Metrics:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8086/dashboard/metrics/1" -Method GET
```

### Automated Testing
```bash
mvn test
```

## 📊 Monitoring & Health Checks

### Actuator Endpoints
- **Health:** `http://localhost:8086/actuator/health`
- **Info:** `http://localhost:8086/actuator/info`
- **Metrics:** `http://localhost:8086/actuator/metrics`
- **Prometheus:** `http://localhost:8086/actuator/prometheus`

### Logging
The service uses SLF4J with the following log levels:
- `com.pulsewatch.dashboard`: DEBUG
- `org.springframework.cloud.openfeign`: DEBUG
- `org.hibernate.SQL`: DEBUG (for SQL queries)

## 🚨 Error Handling

The service includes comprehensive error handling:

1. **Service Communication Errors** - Custom Feign error decoder
2. **Database Connection Errors** - JPA exception handling
3. **Timeout Handling** - Configurable connect/read timeouts
4. **Fallback Responses** - Graceful degradation when services are unavailable
5. **Logging** - Detailed error logging for debugging

## 🔄 Circuit Breaker (Future Enhancement)

Consider adding circuit breaker pattern using:
- Spring Cloud Circuit Breaker
- Resilience4j
- Hystrix (legacy)

## 🐳 Docker Support

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/dashboard-service-1.0.0.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: pulsewatch
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgresql
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  dashboard-service:
    build: .
    ports:
      - "8086:8086"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/pulsewatch
    depends_on:
      - postgres
      - alerting-service
      - ai-smart-alerting
      - threshold-service

volumes:
  postgres_data:
```

## 🔧 Development

### Project Structure
```
src/main/java/com/pulsewatch/dashboard/
├── DashboardServiceApplication.java
├── client/
│   ├── AlertClient.java
│   ├── PredictionClient.java
│   └── ConfigClient.java
├── controller/
│   └── DashboardController.java
├── service/
│   ├── DashboardService.java
│   ├── DatabaseService.java
│   └── impl/
│       ├── DashboardServiceImpl.java
│       └── DatabaseServiceImpl.java
├── entity/
│   └── DashboardMetrics.java
├── repository/
│   └── DashboardMetricsRepository.java
└── config/
    ├── FeignConfig.java
    ├── CustomErrorDecoder.java
    └── DatabaseConfig.java
```

### Adding New Database Entities

1. Create a new entity class in the `entity` package
2. Create a repository interface in the `repository` package
3. Add service methods in `DatabaseService`
4. Implement the service methods in `DatabaseServiceImpl`
5. Add controller endpoints if needed
6. Update documentation

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

1. **Database Connection Errors**
   - Verify PostgreSQL is running on port 5432
   - Check database credentials in `application.yml`
   - Ensure database `pulsewatch` exists

2. **Service Connection Errors**
   - Verify all dependent services are running
   - Check service URLs in configuration
   - Ensure network connectivity

3. **Timeout Issues**
   - Increase timeout values in `application.yml`
   - Check service response times

4. **Port Conflicts**
   - Change `server.port` in `application.yml`
   - Ensure port 8086 is available

### Debug Mode
Enable debug logging by adding to `application.yml`:
```yaml
logging:
  level:
    com.pulsewatch.dashboard: DEBUG
    org.springframework.cloud.openfeign: DEBUG
    org.hibernate.SQL: DEBUG
``` 