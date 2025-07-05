# User Management Service

A Spring Boot microservice for user management with JWT-based authentication and role-based authorization.

## Features

- JWT-based authentication
- Role-based authorization (ADMIN, DEVOPS, VIEWER)
- PostgreSQL database integration
- Spring Security with password encryption
- RESTful API endpoints

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional)

## Setup

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE pulsewatch_users;
```

### 2. Environment Configuration

Copy `env.example` to `.env` and update the values:

```bash
cp env.example .env
```

Update the following variables in `.env`:
- `DB_USERNAME`: PostgreSQL username
- `DB_PASSWORD`: PostgreSQL password
- `JWT_SECRET`: Secret key for JWT token generation

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The service will start on port 8081.

## API Endpoints

### Authentication Endpoints

#### Register User
```
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "VIEWER"
}
```

#### Login User
```
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

### User Management Endpoints

#### Get User by ID
```
GET /users/{id}
Authorization: Bearer <jwt_token>
```

#### Get User by Username
```
GET /users/username/{username}
Authorization: Bearer <jwt_token>
```

## Role-Based Access Control

- **ADMIN**: Full access to all endpoints
- **DEVOPS**: Access to user management endpoints
- **VIEWER**: Read-only access to user data

## Security Features

- JWT token-based authentication
- BCrypt password encryption
- Role-based authorization
- Input validation
- Global exception handling

## Database Schema

The service automatically creates the following table:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Testing

Run the tests:

```bash
mvn test
```

## Docker Support

Build Docker image:

```bash
docker build -t user-management-service .
```

Run with Docker:

```bash
docker run -p 8081:8081 user-management-service
```

## Microservices Architecture

This service is designed to work as part of a microservices architecture:

- **Port**: 8081
- **Database**: PostgreSQL
- **Authentication**: JWT tokens
- **API Gateway**: Compatible with Spring Cloud Gateway
- **Service Discovery**: Compatible with Netflix Eureka

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request 