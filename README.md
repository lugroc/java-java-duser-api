# Full-Stack Microservices Application

A full-stack microservices platform built with **Spring Boot** and **React** — featuring customer authentication, fraud detection, inventory management, and an admin dashboard.

## Architecture

```
React SPA (Vite)
      │  HTTP
      ▼
Spring Cloud Gateway (Port 8765)
      │
      ├──▶ Customer Service (8080) ──▶ PostgreSQL (customer)
      ├──▶ Fraud Service (8081)     ──▶ PostgreSQL (fraud)
      ├──▶ Inventory Service (8082) ──▶ PostgreSQL (inventory)
      ├──▶ Notification Service (8083) ──▶ PostgreSQL (notification)
      │
      └──▶ Eureka Server (8761)
```

## Tech Stack

### Backend (Java)
| Service | Framework | Database | Port |
|---------|-----------|----------|------|
| Eureka Server | Spring Cloud Netflix | — | 8761 |
| API Gateway | Spring Cloud Gateway | — | 8765 |
| Customer | Spring Boot 3.4, JPA, Security | PostgreSQL | 8080 |
| Fraud | Spring Boot 3.4, JPA | PostgreSQL | 8081 |
| Inventory | Spring Boot 3.4, JPA | PostgreSQL | 8082 |
| Notification | Spring Boot 3.4, JPA | PostgreSQL | 8083 |

### Frontend
| Layer | Framework | Port |
|-------|-----------|------|
| SPA | React 19, Vite, React Router 7 | 5173 (dev) |

### DevOps
- Docker & Docker Compose
- GitHub Actions CI
- Swagger / OpenAPI docs per service

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 LTS (for local development)
- Node.js 22 (for local development)

### Run with Docker (recommended)

```bash
# Build and start all services
docker compose up --build

# Wait for all services to start, then access:
# API:        http://localhost:8765 (gateway)
# Eureka:     http://localhost:8761
# pgAdmin:    http://localhost:5050 (admin@example.com / password)
```

### Run locally (development)

```bash
# 1. Start infrastructure
docker compose up postgres

# 2. Start Eureka Server
cd eureka-server && mvn spring-boot:run

# 3. Start microservices (each in separate terminal)
cd customer && mvn spring-boot:run     # port 8080
cd fraud && mvn spring-boot:run        # port 8081
cd inventory && mvn spring-boot:run    # port 8082
cd notification && mvn spring-boot:run # port 8083
cd gateway && mvn spring-boot:run      # port 8765

# 4. Start frontend
cd frontend/client && npm install && npx vite           # React on 5173
```

## Services Overview

### Customer Service (`/api/v1/customers`)
- `POST /api/v1/customers` — Register a new customer
- `POST /api/v1/sessions/login` — Login (returns session ID)
- `GET /api/v1/sessions/validate/{sessionId}` — Validate session
- `POST /api/v1/sessions/logout` — Invalidate session
- **Swagger:** http://localhost:8080/swagger-ui.html

### Inventory Service (`/api/v1/products`)
- `GET /api/v1/products` — List all products
- `POST /api/v1/products` — Create product
- `PUT /api/v1/products/{id}` — Update product
- `DELETE /api/v1/products/{id}` — Delete product
- `GET /api/v1/products/search?name=` — Search products
- **Swagger:** http://localhost:8082/swagger-ui.html

### Fraud Service (`/api/v1/fraud-check`)
- `GET /api/v1/fraud-check/{customerId}` — Check if customer is fraudulent
- **Swagger:** http://localhost:8081/swagger-ui.html

## Testing

```bash
# Java tests (all services)
mvn test

# React tests
cd frontend/client && npm test
```

## CI/CD

GitHub Actions runs on every push to `main`:
1. **Java** — `mvn verify` (all services)
2. **React** — `npm test && npm run build`

## Project Structure

```
├── clients/            # Shared Feign clients and DTOs
├── customer/           # Customer & auth service
├── eureka-server/      # Service discovery
├── fraud/              # Fraud detection
├── gateway/            # API Gateway
├── inventory/          # Product inventory
├── notification/       # Notifications
├── frontend/client/    # React SPA
├── docker/             # Dockerfiles
├── docker-compose.yml  # Full stack orchestration
└── pom.xml             # Maven parent POM
```
