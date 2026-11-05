# Spectrum Manager Backend

Backend platform for managing users, clients, spectrum authorisations, billing, and reports. The project is organized as independent Spring Boot microservices that communicate through Eureka service discovery and are exposed through a Spring Cloud Gateway.

## Architecture

| Component | Directory | Port | Responsibility |
| --- | --- | ---: | --- |
| Eureka Server | `eurekaServer` | `8761` | Service discovery |
| Config Server | `configServer` | `8888` | Centralized configuration |
| User Service | `userService` | `8081` | Users and authentication |
| Authorisation Service | `autorisation-service` | `8082` | Spectrum authorisations |
| API Gateway | `Gateway` | `8083` | Entry point and request routing |
| Billing Service | `facturation-service` | `8084` | Invoices and pricing formulas |
| Reporting Service | `reporting-service` | `8085` | Reports and statistics |
| Client Service | `client-service` | `8086` | Clients and client dashboards |

The services use PostgreSQL. By default, the database configuration points to:

```text
Database: sfmspectre_db
Host: localhost
Port: 5432
User: postgres
```

Set the database password in each service's `src/main/resources/application.properties`. Do not commit credentials.

## Prerequisites

- JDK 17 or newer
- Maven 3.9+ (or the Maven Wrapper included in each service)
- PostgreSQL database named `sfmspectre_db`

## Running locally

Start the infrastructure services first:

```bash
cd eurekaServer
./mvnw spring-boot:run
```

```bash
cd configServer
./mvnw spring-boot:run
```

Then start the application services in separate terminals:

```bash
cd userService && ./mvnw spring-boot:run
cd autorisation-service && ./mvnw spring-boot:run
cd facturation-service && ./mvnw spring-boot:run
cd reporting-service && ./mvnw spring-boot:run
cd client-service && ./mvnw spring-boot:run
cd Gateway && ./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

The Eureka dashboard is available at [http://localhost:8761](http://localhost:8761). The gateway is available at `http://localhost:8083`.

## Gateway routes

Requests can be sent through the gateway using these base paths:

| Path | Service |
| --- | --- |
| `/api/auth/**` | User authentication |
| `/api/users/**` | Users |
| `/api/clients/**` | Clients |
| `/api/client/**` | Client dashboard |
| `/api/autorisations/**` | Authorisations |
| `/api/factures/**` | Invoices |
| `/api/formules/**` | Billing formulas |
| `/api/reports/**` | Reports |

For example:

```bash
curl http://localhost:8083/api/users
curl http://localhost:8083/api/reports/stats
```

## Building and testing

Each service is a standalone Maven project. From a service directory:

```bash
./mvnw clean test
```

To package a service:

```bash
./mvnw clean package
```

Run the command separately for each directory because the repository does not currently contain a root Maven aggregator.

## Project structure

```text
.
├── Gateway/
├── autorisation-service/
├── client-service/
├── configServer/
├── eurekaServer/
├── facturation-service/
├── reporting-service/
└── userService/
```

## Configuration notes

- Eureka clients use `http://localhost:8761/eureka/`.
- Database-backed services use PostgreSQL and Hibernate `ddl-auto=update` by default.
- The gateway exposes actuator endpoints and gateway diagnostics according to its application configuration.
- Review the service-specific `application.properties` files before deploying outside a local development environment.
