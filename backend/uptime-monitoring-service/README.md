# Uptime Monitoring Service

A Spring Boot microservice for monitoring the availability of registered services using HTTP health checks. Provides scheduled monitoring, service registration, and real-time status reporting.

## Features

- **Scheduled Health Checks** - Automatic monitoring every 1 minute
- **Service Registration** - Add services via REST API
- **Real-time Status** - Get current UP/DOWN status of services
- **Response Time Tracking** - Monitor service performance
- **Error Logging** - Detailed error messages for failed checks
- **RESTful API** - Complete CRUD operations for monitored services

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Setup

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE pulsewatch_uptime;
```

### 2. Environment Configuration

Copy the environment example file and configure your settings:

```bash
# Copy the environment example file
cp env.example .env

# Edit the .env file with your database credentials
# The application will automatically load these environment variables
```

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The service will start on port 8083.

## API Endpoints

### Service Registration

#### POST /uptime/register
Register a new service for monitoring:

```json
{
  "name": "User Management Service",
  "url": "http://localhost:8081/actuator/health"
}
```

Response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "User Management Service",
  "url": "http://localhost:8081/actuator/health",
  "status": "UNKNOWN",
  "lastChecked": null,
  "responseTimeMs": null,
  "errorMessage": null,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Service Status

#### GET /uptime/status/{serviceId}
Get current status of a specific service:

```
GET /uptime/status/550e8400-e29b-41d4-a716-446655440000
```

Response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "User Management Service",
  "url": "http://localhost:8081/actuator/health",
  "status": "UP",
  "lastChecked": "2024-01-15T10:35:00Z",
  "responseTimeMs": 245,
  "errorMessage": null,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:35:00Z"
}
```

### Service Management

#### GET /uptime/services
Get all monitored services:

```
GET /uptime/services
```

#### DELETE /uptime/services/{serviceId}
Remove a service from monitoring:

```
DELETE /uptime/services/550e8400-e29b-41d4-a716-446655440000
```

#### POST /uptime/check/{serviceId}
Trigger immediate health check:

```
POST /uptime/check/550e8400-e29b-41d4-a716-446655440000
```

## Service Status Values

- **UP** - Service is responding successfully
- **DOWN** - Service is not responding or returning errors
- **UNKNOWN** - Service has not been checked yet

## Scheduled Monitoring

The service automatically performs health checks every minute using `@Scheduled(fixedRate = 60000)`. Each check:

1. Sends HTTP GET request to the service URL
2. Measures response time
3. Updates status based on HTTP response
4. Logs any errors or timeouts

## Database Schema

The service creates an optimized monitoring table:

```sql
CREATE TABLE monitored_services (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_checked TIMESTAMP,
    response_time_ms BIGINT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Monitoring optimized indexes
CREATE INDEX idx_status ON monitored_services(status);
CREATE INDEX idx_last_checked ON monitored_services(last_checked);
CREATE INDEX idx_name ON monitored_services(name);
```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `postgresql` | PostgreSQL password |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `pulsewatch_uptime` | Database name |
| `SERVER_PORT` | `8083` | Application port |
| `UPTIME_CHECK_INTERVAL_MS` | `60000` | Health check interval (ms) |
| `UPTIME_TIMEOUT_MS` | `5000` | HTTP request timeout (ms) |
| `UPTIME_USER_AGENT` | `PulseWatch-UptimeMonitor/1.0` | User agent for requests |

### Application Properties

```yaml
uptime:
  monitoring:
    interval-ms: 60000      # Health check interval
    timeout-ms: 5000        # HTTP timeout
    user-agent: PulseWatch-UptimeMonitor/1.0
```

## Monitoring Features

### Health Check Logic

1. **HTTP GET Request** - Sends GET request to service URL
2. **Response Validation** - Checks for 2xx status codes
3. **Timeout Handling** - Configurable timeout (default 5s)
4. **Error Tracking** - Stores detailed error messages
5. **Performance Monitoring** - Tracks response times

### Logging

- **Console Output** - Real-time monitoring logs
- **File Logging** - Persistent logs in `logs/uptime-monitoring-service.log`
- **Log Rotation** - 10MB files, 30 days retention
- **Structured Logging** - JSON format for production

## Validation and Error Handling

### URL Validation
The service validates URLs in the service layer using Java's built-in URL validation:
- **@NotBlank** validation ensures non-empty strings
- **Service layer validation** ensures proper URL format using `new URL(url).toURI()`
- **Error handling** returns appropriate HTTP status codes for validation failures

### Error Responses
- **400 Bad Request** - Invalid URL format or missing required fields
- **404 Not Found** - Service not found
- **500 Internal Server Error** - Unexpected errors

## Testing

### Quick Test Scripts

**Bash (Linux/Mac):**
```bash
chmod +x test-endpoints.sh
./test-endpoints.sh
```

**PowerShell (Windows):**
```powershell
.\test-endpoints.ps1
```

### Manual Testing

1. **Register a service:**
   ```bash
   curl -X POST http://localhost:8083/uptime/register \
     -H "Content-Type: application/json" \
     -d '{"name": "Test Service", "url": "http://httpbin.org/status/200"}'
   ```

2. **Check service status:**
   ```bash
   curl http://localhost:8083/uptime/status/{serviceId}
   ```

3. **List all services:**
   ```bash
   curl http://localhost:8083/uptime/services
   ```

### Automated Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UptimeControllerTest
```

## Production Deployment

### Health Check Endpoints

The service includes Spring Boot Actuator endpoints:

- Health check: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Info: `GET /actuator/info`

### Monitoring Integration

The service can be monitored by:
- **Prometheus** - Metrics endpoint available
- **Grafana** - Dashboard integration
- **Alerting** - Configure alerts for service downtime

## Microservices Architecture

This service is designed to work as part of a microservices architecture:

- **Port**: 8083
- **Database**: PostgreSQL with monitoring optimization
- **API**: RESTful endpoints for service management
- **Scheduling**: Spring Boot scheduling for health checks
- **Integration**: Compatible with other PulseWatch services

## Troubleshooting

### Common Issues

1. **Service not responding:**
   - Check if service URL is accessible
   - Verify network connectivity
   - Check service logs

2. **Database connection issues:**
   - Verify PostgreSQL is running
   - Check database credentials
   - Ensure database exists

3. **Scheduling not working:**
   - Verify `@EnableScheduling` is present
   - Check application logs for errors
   - Verify Spring Boot version compatibility

### Log Analysis

Monitor the application logs for:
- Health check results
- Service registration events
- Error messages and timeouts
- Performance metrics 