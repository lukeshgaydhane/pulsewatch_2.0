# System Monitoring Service

A Spring Boot microservice for collecting and storing system metrics (CPU, RAM, Disk, Network) with time-series database optimization. Designed to receive data from external agents and provide real-time monitoring capabilities.

## Features

- **Time-series optimized database schema** with PostgreSQL
- **REST API endpoints** for metric collection and retrieval
- **Support for external agents** (Python scripts, monitoring tools)
- **Real-time metrics storage** with efficient indexing
- **Multiple metric types**: CPU, Memory, Disk, Network
- **Flexible data format** with additional metadata support

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Setup

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE pulsewatch_monitoring;
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

The service will start on port 8082.

## API Endpoints

### Collect Metrics (External Agents)

#### POST /metrics
Receive metrics data from external agents:

```json
{
  "host": "server-01",
  "timestamp": "2024-01-15T10:30:00",
  "metrics": [
    {
      "metricType": "CPU",
      "metricName": "cpu_usage",
      "metricValue": 75.5,
      "unit": "%",
      "additionalData": "{\"core\": 0}"
    },
    {
      "metricType": "MEMORY",
      "metricName": "memory_usage",
      "metricValue": 8.2,
      "unit": "GB"
    }
  ]
}
```

### Retrieve Metrics

#### GET /metrics/cpu
Get CPU metrics (last hour by default):
```
GET /metrics/cpu?startTime=2024-01-15T09:30:00
```

#### GET /metrics/memory
Get memory metrics:
```
GET /metrics/memory?startTime=2024-01-15T09:30:00
```

#### GET /metrics/disk
Get disk metrics:
```
GET /metrics/disk?startTime=2024-01-15T09:30:00
```

#### GET /metrics/network
Get network metrics:
```
GET /metrics/network?startTime=2024-01-15T09:30:00
```

#### GET /metrics/{metricType}
Get metrics by type:
```
GET /metrics/cpu?startTime=2024-01-15T09:30:00
```

#### GET /metrics/{metricType}/average
Get average metric value:
```
GET /metrics/cpu/average?startTime=2024-01-15T09:30:00
```

## Database Schema

The service creates an optimized time-series table:

```sql
CREATE TABLE system_metrics (
    id BIGSERIAL PRIMARY KEY,
    host VARCHAR(255) NOT NULL,
    metric_type VARCHAR(20) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    metric_value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    additional_data TEXT
);

-- Time-series optimized indexes
CREATE INDEX idx_timestamp ON system_metrics(timestamp);
CREATE INDEX idx_metric_type_timestamp ON system_metrics(metric_type, timestamp);
CREATE INDEX idx_host_timestamp ON system_metrics(host, timestamp);
```

## Example Python Agent

```python
import requests
import psutil
import json
from datetime import datetime

def collect_metrics():
    metrics = []
    
    # CPU metrics
    cpu_percent = psutil.cpu_percent(interval=1)
    metrics.append({
        "metricType": "CPU",
        "metricName": "cpu_usage",
        "metricValue": cpu_percent,
        "unit": "%"
    })
    
    # Memory metrics
    memory = psutil.virtual_memory()
    metrics.append({
        "metricType": "MEMORY",
        "metricName": "memory_usage",
        "metricValue": memory.percent,
        "unit": "%"
    })
    
    # Disk metrics
    disk = psutil.disk_usage('/')
    metrics.append({
        "metricType": "DISK",
        "metricName": "disk_usage",
        "metricValue": disk.percent,
        "unit": "%"
    })
    
    return {
        "host": "server-01",
        "timestamp": datetime.now().isoformat(),
        "metrics": metrics
    }

# Send metrics to the service
def send_metrics():
    data = collect_metrics()
    response = requests.post(
        'http://localhost:8082/metrics',
        json=data,
        headers={'Content-Type': 'application/json'}
    )
    print(f"Response: {response.status_code}")

if __name__ == "__main__":
    send_metrics()
```

## Time-Series Optimizations

- **Efficient indexing** on timestamp and metric_type columns
- **Batch inserts** for high-throughput scenarios
- **Partitioning-ready schema** for large datasets
- **Optimized queries** for time-range lookups

## Monitoring

The service includes Spring Boot Actuator endpoints:

- Health check: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Info: `GET /actuator/info`

## Testing

Run the tests:

```bash
mvn test
```

### Verify Lombok Compilation

If you encounter "cannot find symbol" errors for getter/setter methods, run:

```bash
# On Linux/Mac
chmod +x verify-compilation.sh
./verify-compilation.sh

# On Windows
mvn clean compile test
```

This ensures Lombok annotations are properly processed.

### Logging Configuration

The application uses SLF4J with Logback for logging. If you encounter "cannot find symbol: variable log" errors:

1. **Check logger declarations** - Each class should have:
   ```java
   private static final Logger log = LoggerFactory.getLogger(ClassName.class);
   ```

2. **Verify imports** - Ensure you have:
   ```java
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   ```

3. **Test logging** - Run the logging test:
   ```bash
   mvn test -Dtest=LoggingTest
   ```

4. **Check log files** - Logs are written to `logs/system-monitoring-service.log`

## Environment Variables

The application supports the following environment variables (with defaults):

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `postgresql` | PostgreSQL password |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `pulsewatch_monitoring` | Database name |
| `SERVER_PORT` | `8082` | Application port |
| `JPA_HIBERNATE_DDL_AUTO` | `update` | Hibernate DDL mode |
| `JPA_SHOW_SQL` | `true` | Show SQL queries |
| `LOGGING_LEVEL_COM_PULSEWATCH_MONITORING` | `DEBUG` | Application logging level |
| `LOGGING_LEVEL_ORG_HIBERNATE_SQL` | `DEBUG` | Hibernate SQL logging |

## Microservices Architecture

This service is designed to work as part of a microservices architecture:

- **Port**: 8082
- **Database**: PostgreSQL with time-series optimization
- **API**: RESTful endpoints for metric collection and retrieval
- **Integration**: Compatible with external monitoring agents 