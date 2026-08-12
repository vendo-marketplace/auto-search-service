# Auto Search Service

The **Auto Search Service** is responsible for automatically searching and discovering relevant products on the Vendo platform. It analyzes search requests and manages the process of finding suitable products based on the user's input and search criteria.

This service is designed to automate product discovery and integrate with the Vendo search infrastructure to provide relevant product results.

---

# Tech Stack

* Java 17
* Spring Boot
* OpenFeign
* Docker
* Eureka
* Zipkin
* Micrometer
* Lombok
* Maven
* JUnit 5
* Mockito

---

# Architecture

The service follows **Hexagonal Architecture (Ports and Adapters)** to isolate the core auto-search logic from external frameworks, databases, message brokers, and other microservices.

## Layers

**domain**  
Contains the core business rules, models, and domain-specific logic.

**application**  
Contains application use cases and orchestration logic.

**port**  
Defines interfaces used to communicate with external systems.

**adapter**  
Contains implementations of external integrations.

- **adapter.in**: Entry points such as REST controllers and messaging consumers.
- **adapter.out**: Outgoing integrations such as search clients and external services.

**infrastructure**  
Contains framework-specific configurations and bean definitions.

- Configurations for OpenAPI, Eureka, Kafka, OpenFeign, Elasticsearch, and MapStruct.

# Prerequisites

Before running this service, ensure the required infrastructure and core services are up.

## Dependencies

This service depends on:

- **Config Server** – provides externalized configuration
- **Service Registry (Eureka)** – provides service discovery
- **Search Service** – provides product search functionality.
- **Kafka** – provides asynchronous communication.
- **Elasticsearch** – used by the search infrastructure for product discovery.

---

# Running the Service

---

## 1. Clone and run Config Server

```
git clone https://github.com/vendo-marketplace/config-server
cd config-server
mvn spring-boot:run
```


---

## 2. Clone and run Service Registry

```
git clone https://github.com/vendo-marketplace/registry-service
cd registry-service
mvn spring-boot:run
```


# Running the Service

---

## 3. Run application

Or build and run:

```
mvn clean package
java -jar target/auth-service.jar
```

---

# Environment Variables

| Variable          | Description       | Default   |
|-------------------|-------------------|-----------|
| CONFIG_SERVER_URL | Config server url | 8010      |

---

# API Documentation

Swagger UI:

```
http://194.163.130.14:8030/swagger-ui/index.html
```

---

# Running Tests

Run all tests

```
mvn test
```

Run integration tests

```
mvn verify
```

---

# Code Style

The project follows standard **Java code conventions**.

Key principles:

* Clean Architecture
* SOLID principles
* Immutable DTOs
* Constructor injection
* Clear separation between layers

---

# Contributing

1. Create feature branch
2. Write tests
3. Ensure tests pass
4. Create pull request
