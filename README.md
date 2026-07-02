# Microservices Application

A microservices platform built with **Spring Boot** — featuring customer authentication, fraud detection, inventory management, and notifications.

## Architecture (local dev)

```
Client (HTTP / Postman)
      │
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

## Services

| Service | Port | Database |
|---------|------|----------|
| Eureka Server | 8761 | — |
| API Gateway | 8765 | — |
| Customer | 8080 | PostgreSQL |
| Fraud | 8081 | PostgreSQL |
| Inventory | 8082 | PostgreSQL |
| Notification | 8083 | PostgreSQL |

## Quick Start

```bash
docker compose up --build
```

Then access:
- API: `http://localhost:8765`
- Eureka: `http://localhost:8761`
- pgAdmin: `http://localhost:5050`

## Project Structure

```
├── clients/            # Shared Feign clients and DTOs
├── customer/           # Customer & auth service
├── eureka-server/      # Service discovery
├── fraud/              # Fraud detection
├── gateway/            # API Gateway
├── inventory/          # Product inventory
├── notification/       # Notifications
├── aws-monolith/       # Single-jar deployable for EC2
├── deploy/             # Deployment scripts
├── docker/             # Dockerfiles
├── docker-compose.yml  # Full stack orchestration
└── pom.xml             # Maven parent POM
```
