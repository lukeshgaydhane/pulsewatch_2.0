-# PulseWatch Service Discovery (Eureka Server)

This microservice provides service discovery for the PulseWatch microservices architecture using Spring Cloud Netflix Eureka Server.

## Features
- Eureka Server for service registration and discovery
- Java 17, Spring Boot 3.x, Spring Cloud 2022.x
- Runs on port 8761

## Build

```
mvn clean install
```

## Run

```
mvn spring-boot:run
```

Or build the jar and run:

```
java -jar target/service-discovery-0.0.1-SNAPSHOT.jar
```

## Configuration

The main configuration is in `src/main/resources/application.yml`:

```yaml
server:
  port: 8761
spring:
  application:
    name: service-discovery
instance:
  hostname: localhost
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

## Eureka Dashboard

Once running, access the Eureka dashboard at:

[http://localhost:8761/](http://localhost:8761/)

## Registering Other Services

- Add the following dependencies to each microservice's `pom.xml`:
  - `spring-cloud-starter-netflix-eureka-client`
- Annotate the main application class with `@EnableEurekaClient` (Spring Boot 3.x: just the dependency is enough, but annotation is still supported).
- Configure each service's `application.yml`:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## References
- [Spring Cloud Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/) 