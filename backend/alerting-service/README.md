# Alerting Service

A Spring Boot microservice for sending alerts via email, WhatsApp (using Gupshup API), and in-app notifications. The service integrates with RabbitMQ to receive alert trigger events from other microservices and provides comprehensive alert history and statistics.

## Features

- **Multi-channel Alerting**: Email, WhatsApp, and in-app notifications
- **RabbitMQ Integration**: Asynchronous alert processing via message queues
- **Alert History**: Complete audit trail of all sent alerts
- **Filtering & Statistics**: Advanced querying and analytics capabilities
- **RESTful API**: Comprehensive REST endpoints for alert management
- **Monitoring**: Spring Boot Actuator integration with Prometheus metrics

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- RabbitMQ 3.8+

## Setup

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE pulsewatch;
```

### 2. RabbitMQ Setup

Ensure RabbitMQ is running and accessible. The service will automatically create the required queues and exchanges.

### 3. Environment Configuration

Copy the environment example file and configure your settings:

```bash
# Copy the environment example file
cp env.example .env

# Edit the .env file with your actual credentials
# The application will automatically load these environment variables
```

### 4. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The service will start on port 8084.

## API Endpoints

### Send Alert

#### POST /alerts/send
Send an alert immediately:

```json
{
  "type": "EMAIL",
  "message": "CPU usage exceeded 80% on server-01",
  "recipient": "admin@example.com"
}
```

Response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "type": "EMAIL",
  "message": "CPU usage exceeded 80% on server-01",
  "recipient": "admin@example.com",
  "sentAt": "2024-01-15T10:30:00Z",
  "success": true,
  "errorMessage": null,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### Alert History

#### GET /alerts/history
Get alert history with optional filtering:

```
GET /alerts/history?type=EMAIL&startTime=2024-01-15T00:00:00Z&endTime=2024-01-15T23:59:59Z
```

Query Parameters:
- `type`: Filter by alert type (EMAIL, WHATSAPP, IN_APP)
- `startTime`: Start time for filtering (ISO format)
- `endTime`: End time for filtering (ISO format)
- `recipient`: Filter by recipient

#### GET /alerts/history/{alertId}
Get specific alert by ID:

```
GET /alerts/history/550e8400-e29b-41d4-a716-446655440000
```

### Alert Statistics

#### GET /alerts/stats
Get alert statistics:

```
GET /alerts/stats?startTime=2024-01-15T00:00:00Z&endTime=2024-01-15T23:59:59Z
```

Response:
```json
{
  "totalAlerts": 150,
  "successfulEmails": 45,
  "successfulWhatsApps": 30,
  "successfulInApps": 75,
  "startTime": "2024-01-15T00:00:00Z",
  "endTime": "2024-01-15T23:59:59Z"
}
```

## Alert Types

### Email Alerts
- **Type**: `EMAIL`
- **Recipient**: Email address
- **Configuration**: SMTP settings in application.yml

### WhatsApp Alerts
- **Type**: `WHATSAPP`
- **Recipient**: Phone number (with country code)
- **Provider**: Gupshup API
- **Configuration**: Gupshup API credentials in application.yml

### In-App Alerts
- **Type**: `IN_APP`
- **Recipient**: User identifier or system component
- **Storage**: Saved to database for retrieval

## RabbitMQ Integration

### Queue Configuration
- **Queue Name**: `alerts.queue` (configurable)
- **Exchange**: `alerts.exchange` (Topic exchange)
- **Routing Key**: `alerts.routing.key`

### Message Format
```json
{
  "type": "EMAIL",
  "message": "Service monitoring alert",
  "recipient": "user@example.com"
}
```

### Publishing Messages
Other services can publish alert messages to RabbitMQ:

```java
@Autowired
private RabbitTemplate rabbitTemplate;

public void sendAlert(String type, String message, String recipient) {
    SendAlertRequest request = new SendAlertRequest();
    request.setType(type);
    request.setMessage(message);
    request.setRecipient(recipient);
    
    rabbitTemplate.convertAndSend("alerts.exchange", "alerts.routing.key", request);
}
```

## Database Schema

The service creates an optimized alerts table:

```sql
CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    success BOOLEAN NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_type ON alerts(type);
CREATE INDEX idx_sent_at ON alerts(sent_at);
CREATE INDEX idx_recipient ON alerts(recipient);
CREATE INDEX idx_success ON alerts(success);
```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `postgresql` | PostgreSQL password |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `pulsewatch_alerts` | Database name |
| `SERVER_PORT` | `8084` | Application port |
| `RABBITMQ_HOST` | `localhost` | RabbitMQ host |
| `RABBITMQ_PORT` | `5672` | RabbitMQ port |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP host |
| `MAIL_USERNAME` | `your-email@gmail.com` | SMTP username |
| `MAIL_PASSWORD` | `your-app-password` | SMTP password |
| `GUPSHUP_API_KEY` | `your-gupshup-api-key` | Gupshup API key |
| `GUPSHUP_SOURCE_NUMBER` | `your-whatsapp-number` | WhatsApp source number |

### Application Properties

```yaml
alerting:
  gupshup:
    api-key: ${GUPSHUP_API_KEY}
    api-url: ${GUPSHUP_API_URL}
    source-number: ${GUPSHUP_SOURCE_NUMBER}
    app-name: ${GUPSHUP_APP_NAME}
  
  thresholds:
    cpu: ${CPU_THRESHOLD:80}
    memory: ${MEMORY_THRESHOLD:85}
  
  rabbitmq:
    queue:
      name: ${ALERT_QUEUE_NAME:alerts.queue}
      exchange: ${ALERT_EXCHANGE_NAME:alerts.exchange}
      routing-key: ${ALERT_ROUTING_KEY:alerts.routing.key}
```

## Integration Examples

### System Monitoring Integration
```java
// In system-monitoring-service
@Scheduled(fixedRate = 60000)
public void checkSystemMetrics() {
    if (cpuUsage > threshold) {
        alertService.sendAlert("EMAIL", 
            "CPU usage exceeded " + threshold + "% on " + hostname, 
            "admin@company.com");
    }
}
```

### Uptime Monitoring Integration
```java
// In uptime-monitoring-service
private void markServiceAsDown(MonitoredService service, String errorMessage) {
    service.setStatus(ServiceStatus.DOWN);
    // ... existing code ...
    
    // Send alert
    rabbitTemplate.convertAndSend("alerts.exchange", "alerts.routing.key", 
        new SendAlertRequest("WHATSAPP", 
            "Service " + service.getName() + " is DOWN: " + errorMessage, 
            "+1234567890"));
}
```

## Testing

### Manual Testing

1. **Send Email Alert:**
   ```bash
   curl -X POST http://localhost:8084/alerts/send \
     -H "Content-Type: application/json" \
     -d '{"type": "EMAIL", "message": "Test alert", "recipient": "test@example.com"}'
   ```

2. **Send WhatsApp Alert:**
   ```bash
   curl -X POST http://localhost:8084/alerts/send \
     -H "Content-Type: application/json" \
     -d '{"type": "WHATSAPP", "message": "Test alert", "recipient": "+1234567890"}'
   ```

3. **Get Alert History:**
   ```bash
   curl http://localhost:8084/alerts/history?type=EMAIL
   ```

### Automated Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=AlertControllerTest
```

## Monitoring

### Health Check
```
GET /actuator/health
```

### Metrics
```
GET /actuator/metrics
GET /actuator/prometheus
```

### Custom Metrics
The service exposes custom metrics:
- `alerts_sent_total` - Total alerts sent
- `alerts_sent_by_type` - Alerts by type
- `alerts_success_rate` - Success rate by type

## Production Deployment

### Docker
```dockerfile
FROM openjdk:17-jre-slim
COPY target/alerting-service-1.0.0.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: alerting-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: alerting-service
  template:
    metadata:
      labels:
        app: alerting-service
    spec:
      containers:
      - name: alerting-service
        image: pulsewatch/alerting-service:latest
        ports:
        - containerPort: 8084
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: alerting-config
              key: db.host
```

## Troubleshooting

### Common Issues

1. **Email not sending:**
   - Check SMTP credentials
   - Verify email provider settings (Gmail requires app passwords)
   - Check firewall/network connectivity

2. **WhatsApp not sending:**
   - Verify Gupshup API key
   - Check phone number format (include country code)
   - Ensure WhatsApp Business API is properly configured

3. **RabbitMQ connection issues:**
   - Verify RabbitMQ is running
   - Check connection credentials
   - Ensure queue permissions

4. **Database connection issues:**
   - Verify PostgreSQL is running
   - Check database credentials
   - Ensure database exists

### Log Analysis

Monitor the application logs for:
- Alert sending results
- RabbitMQ message processing
- Database operation errors
- External API failures

## Architecture

The service follows a clean architecture pattern:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │     Service     │    │   Repository    │
│                 │    │                 │    │                 │
│  REST Endpoints │───▶│  Alert Logic    │───▶│   Data Access   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Validation    │    │  External APIs  │    │   PostgreSQL    │
│                 │    │                 │    │                 │
│  Request DTOs   │    │ Email/WhatsApp  │    │   Alert Table   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Contributing

1. Follow the existing code style
2. Add tests for new features
3. Update documentation
4. Ensure all tests pass before submitting

## License

This project is part of the PulseWatch monitoring platform. 