#!/bin/bash

# Navigate to kafka-lab directory
cd "$(dirname "$0")"

echo "=== Building all microservices ==="

# Build API Gateway
echo "Building API Gateway..."
cd api-gateway
mvn clean package -DskipTests
cd ..

# Build Order Service
echo "Building Order Service..."
cd order-service
mvn clean package -DskipTests
cd ..

# Build Inventory Service
echo "Building Inventory Service..."
cd inventory-service
mvn clean package -DskipTests
cd ..

# Build Billing Service
echo "Building Billing Service..."
cd billing-service
mvn clean package -DskipTests
cd ..

echo "=== Build complete ==="
echo ""
echo "To start all services, run:"
echo "cd kafka-lab && docker-compose up -d --build"
