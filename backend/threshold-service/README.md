# Threshold Service

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13+-blue.svg)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A microservice for managing system monitoring thresholds and alert configurations. This service allows users to set custom thresholds for CPU, RAM, and other system metrics, along with their preferred notification methods.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

The Threshold Service is a core component of the Pulsewatch monitoring system that manages user-specific threshold configurations for system metrics. It provides RESTful APIs to create, retrieve, and update threshold settings that determine when alerts should be triggered.

### What Problem Does It Solve?

- **Centralized Threshold Management**: Provides a single source of truth for all user threshold configurations
- **Customizable Alerting**: Allows users to set personalized thresholds for different metrics
- **Multi-Channel Notifications**: Supports email, SMS, and push notification preferences
- **Scalable Architecture**: Built as a microservice for easy scaling and maintenance

## ✨ Features

- **RESTful API**: Clean, REST-compliant endpoints for threshold management
- **PostgreSQL Integration**: Robust data persistence with PostgreSQL
- **JPA/Hibernate**: Object-relational mapping for easy database operations
- **Spring Boot Actuator**: Built-in monitoring and health checks
- **Validation**: Input validation and error handling
- **Lombok**: Reduces boilerplate code
- **DevTools**: Enhanced development experience

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Client Apps   │───▶│  Threshold API   │───▶│   PostgreSQL    │
│                 │    │   (Port 8085)    │    │   Database      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌──────────────────┐
                       │   Alert Service  │
                       │   Integration    │
                       └──────────────────┘
```

### Technology Stack

- **Framework**: Spring Boot 3.2.6
- **Language**: Java 17
- **Database**: PostgreSQL 13+
- **Build Tool**: Maven 3.6+
- **ORM**: Spring Data JPA with Hibernate
- **Documentation**: OpenAPI/Swagger

## 📋 Prerequisites

Before running this service, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6** or higher
- **PostgreSQL 13** or higher
- **Git** (for cloning the repository)

### System Requirements

- **Memory**: Minimum 512MB RAM
- **Storage**: 1GB free disk space
- **Network**: Port 8085 available

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd backend/threshold-service
```

### 2. Database Setup

Create the PostgreSQL database:

```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database
CREATE DATABASE pulsewatch;

-- Verify database creation
\l
```

### 3. Build the Application

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

### 4. Run the Service

#### Development Mode
```bash
mvn spring-boot:run
```

#### Production Mode
```bash
java -jar target/threshold-service-0.0.1-SNAPSHOT.jar
```

The service will start on `http://localhost:8085`

## ⚙️ Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8085` | Application port |
| `DB_HOST` | `localhost` | Database host |
| `DB_PORT` | `5432` | Database port |
| `DB_NAME` | `pulsewatch` | Database name |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgresql` | Database password |

### Application Properties

Key configuration options in `application.yml`:

```yaml
server:
  port: 8085

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pulsewatch
    username: postgres
    password: postgresql
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

## 📚 API Documentation

### Base URL
```
http://localhost:8085
```

### Endpoints

#### 1. Create/Update Threshold Configuration

**POST** `/config/threshold`

Creates or updates a threshold configuration for a user.

**Request Body:**
```json
{
  "userId": 123,
  "cpuThreshold": 85,
  "ramThreshold": 80,
  "emailAlert": true,
  "smsAlert": false,
  "pushAlert": true
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "cpuThreshold": 85,
  "ramThreshold": 80,
  "emailAlert": true,
  "smsAlert": false,
  "pushAlert": true
}
```

#### 2. Get Threshold Configuration

**GET** `/config/{userId}`

Retrieves threshold configuration for a specific user.

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "cpuThreshold": 85,
  "ramThreshold": 80,
  "emailAlert": true,
  "smsAlert": false,
  "pushAlert": true
}
```

**Status Codes:**
- `200 OK`: Configuration found
- `404 Not Found`: No configuration for user

### Testing with cURL

```bash
# Create threshold configuration
curl -X POST http://localhost:8085/config/threshold \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "cpuThreshold": 85,
    "ramThreshold": 80,
    "emailAlert": true,
    "smsAlert": false,
    "pushAlert": true
  }'

# Get threshold configuration
curl -X GET http://localhost:8085/config/123
```

## 🛠️ Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/pulsewatch/threshold/
│   │       ├── ThresholdServiceApplication.java
│   │       ├── controller/
│   │       │   └── ThresholdConfigController.java
│   │       ├── entity/
│   │       │   └── ThresholdConfig.java
│   │       ├── repository/
│   │       │   └── ThresholdConfigRepository.java
│   │       └── service/
│   │           └── ThresholdConfigService.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── com/pulsewatch/threshold/
```

### Adding New Features

1. **Entity**: Add new fields to `ThresholdConfig.java`
2. **Repository**: Add query methods to `ThresholdConfigRepository.java`
3. **Service**: Add business logic to `ThresholdConfigService.java`
4. **Controller**: Add REST endpoints to `ThresholdConfigController.java`

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public methods
- Keep methods small and focused

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=ThresholdConfigServiceTest
```

### Test Coverage

The project includes unit tests for:
- Service layer business logic
- Repository data access
- Controller endpoint validation

## 🚀 Deployment

### Docker Deployment

1. **Build Docker Image**
```bash
docker build -t threshold-service:latest .
```

2. **Run Container**
```bash
docker run -d \
  --name threshold-service \
  -p 8085:8085 \
  -e DB_HOST=your-db-host \
  -e DB_PASSWORD=your-db-password \
  threshold-service:latest
```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: threshold-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: threshold-service
  template:
    metadata:
      labels:
        app: threshold-service
    spec:
      containers:
      - name: threshold-service
        image: threshold-service:latest
        ports:
        - containerPort: 8085
        env:
        - name: DB_HOST
          value: "postgres-service"
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
```

### Production Checklist

- [ ] Set up proper logging configuration
- [ ] Configure database connection pooling
- [ ] Set up monitoring and alerting
- [ ] Configure SSL/TLS certificates
- [ ] Set up backup and recovery procedures
- [ ] Configure rate limiting
- [ ] Set up API documentation

## 📊 Monitoring

### Health Checks

The service includes Spring Boot Actuator endpoints:

- **Health Check**: `GET /actuator/health`
- **Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`

### Logging

Configure logging in `application.yml`:

```yaml
logging:
  level:
    com.pulsewatch.threshold: INFO
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/threshold-service.log
```

### Metrics

Key metrics to monitor:
- Request count and response times
- Database connection pool usage
- Error rates
- Memory and CPU usage

## 🔧 Troubleshooting

### Common Issues

#### 1. Database Connection Failed

**Error**: `Connection refused: connect`

**Solution**:
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql

# Start PostgreSQL if stopped
sudo systemctl start postgresql

# Verify database exists
psql -U postgres -l
```

#### 2. Port Already in Use

**Error**: `Web server failed to start. Port 8085 was already in use`

**Solution**:
```bash
# Find process using port 8085
netstat -ano | findstr :8085

# Kill the process
taskkill /PID <process-id> /F
```

#### 3. Memory Issues

**Error**: `java.lang.OutOfMemoryError`

**Solution**:
```bash
# Increase heap size
java -Xmx2g -jar target/threshold-service-0.0.1-SNAPSHOT.jar
```

### Debug Mode

Enable debug logging:

```yaml
logging:
  level:
    com.pulsewatch.threshold: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Write unit tests for new features
- Follow existing code style
- Update documentation for API changes
- Ensure all tests pass before submitting PR

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:

- **Issues**: [GitHub Issues](https://github.com/your-org/pulsewatch/issues)
- **Documentation**: [Wiki](https://github.com/your-org/pulsewatch/wiki)
- **Email**: support@pulsewatch.com

---

**Built with ❤️ by the Pulsewatch Team** 