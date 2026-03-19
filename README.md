# Microservices Kafka Lab

An event-driven microservices architecture using Spring Boot, Spring Cloud Gateway, Apache Kafka, and H2 database.

## Architecture Overview

This project implements a distributed system with four microservices:

- **API Gateway** (Port 8080) - Entry point for all client requests
- **Order Service** (Port 8081) - Handles order creation and publishes events to Kafka
- **Inventory Service** (Port 8082) - Consumes order events and manages inventory
- **Billing Service** (Port 8083) - Consumes order events and generates invoices

### Technology Stack

- Spring Boot 3.2.0
- Spring Cloud Gateway 2023.0.0
- Apache Kafka
- H2 In-Memory Database
- Java 21

## Project Structure

```
microservices-kafka-lab/
├── docker-compose.yml          # Kafka and Zookeeper configuration
├── api-gateway/               # API Gateway Service
│   ├── pom.xml
│   └── src/main/java/com/example/apigateway/
│       ├── ApiGatewayApplication.java
│       └── controller/FallbackController.java
├── order-service/             # Order Service
│   ├── pom.xml
│   └── src/main/java/com/example/orderservice/
│       ├── OrderServiceApplication.java
│       ├── entity/Order.java
│       ├── dto/OrderRequest.java, OrderResponse.java
│       ├── repository/OrderRepository.java
│       ├── service/OrderService.java
│       ├── controller/OrderController.java
│       └── kafka/OrderProducer.java, OrderConsumer.java
├── inventory-service/        # Inventory Service
│   ├── pom.xml
│   └── src/main/java/com/example/inventoryservice/
│       ├── InventoryServiceApplication.java
│       ├── entity/Inventory.java
│       ├── repository/InventoryRepository.java
│       ├── service/InventoryService.java
│       ├── controller/InventoryController.java
│       ├── consumer/InventoryConsumer.java
│       └── event/OrderEvent.java
├── billing-service/         # Billing Service
│   ├── pom.xml
│   └── src/main/java/com/example/billingservice/
│       ├── BillingServiceApplication.java
│       ├── entity/Invoice.java
│       ├── repository/InvoiceRepository.java
│       ├── controller/BillingController.java
│       ├── consumer/BillingConsumer.java
│       └── event/OrderEvent.java
└── postman/                 # Postman Collection
    └── microservices-kafka-lab.postman_collection.json
```

## Execution Steps

### 1. Start Kafka

```bash
cd microservices-kafka-lab
docker-compose up -d
```

### 2. Build all services

You can build all services using the provided build script:

```bash
# Make the script executable (if not already)
chmod +x build-all.sh

# Run the build script
./build-all.sh
```

Alternatively, you can build them manually:

```bash
# Go to root directory
cd microservices-kafka-lab

# Build Order Service
cd order-service && mvn clean package && cd ..

# Build Inventory Service
cd inventory-service && mvn clean package && cd ..

# Build Billing Service
cd billing-service && mvn clean package && cd ..

# Build API Gateway
cd api-gateway && mvn clean package && cd ..
```


### 3. Start all services

Open a new terminal for each service:

```bash
# Start Order Service
cd order-service
./mvnw spring-boot:run

# Start Inventory Service (new terminal)
cd inventory-service
./mvnw spring-boot:run

# Start Billing Service (new terminal)
cd billing-service
./mvnw spring-boot:run

# Start API Gateway (new terminal)
cd api-gateway
./mvnw spring-boot:run
```

### 4. Test the flow

1. Import the Postman collection from `postman/microservices-kafka-lab.postman_collection.json`
2. Send a POST request to create an order
3. Check inventory and billing services to verify event consumption

## Testing URLs

| Service | Endpoint | Method |
|---------|----------|--------|
| Create Order | http://localhost:8080/api/orders | POST |
| Get All Inventory | http://localhost:8080/api/inventory | GET |
| Get Inventory by Product | http://localhost:8080/api/inventory/{product} | GET |
| Add Inventory | http://localhost:8080/api/inventory | POST |
| Get All Invoices | http://localhost:8080/api/invoices | GET |
| Get Invoice by ID | http://localhost:8080/api/invoices/{id} | GET |
| Get Invoices by Order ID | http://localhost:8080/api/invoices/order/{orderId} | GET |

## H2 Database Consoles

| Service | URL |
|---------|-----|
| Order Service | http://localhost:8081/h2-console |
| Inventory Service | http://localhost:8082/h2-console |
| Billing Service | http://localhost:8083/h2-console |

**JDBC URL for all:** `jdbc:h2:mem:{dbname}`
- Order: `jdbc:h2:mem:orderdb`
- Inventory: `jdbc:h2:mem:inventorydb`
- Billing: `jdbc:h2:mem:billingdb`

**Username:** sa
**Password:** password

## Kafka Topics

- `order-topic` - Main topic for order events

## Troubleshooting

### Ensure Kafka is running
```bash
docker ps
```

### Check Kafka logs
```bash
docker logs kafka-kraft
```

### Verify Kafka topic creation
```bash
docker exec -it kafka-kraft kafka-topics.sh --list --bootstrap-server localhost:9092
```

## How It Works

1. **Client Request**: POST to `/api/orders` via API Gateway
2. **Order Service**: Saves order to H2 database and publishes event to Kafka `order-topic`
3. **Inventory Service**: Consumes order event from Kafka, updates inventory in H2 database
4. **Billing Service**: Consumes order event from Kafka, creates invoice in H2 database

```
Client → API Gateway → Order Service → Kafka → Inventory Service
                                      ↘→ Billing Service
```
