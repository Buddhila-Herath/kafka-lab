#!/bin/bash

echo "Building all microservices..."

echo "Building Order Service..."
cd order-service
../mvnw clean package
cd ..

echo "Building Inventory Service..."
cd inventory-service
../mvnw clean package
cd ..

echo "Building Billing Service..."
cd billing-service
../mvnw clean package
cd ..

echo "Building API Gateway..."
cd api-gateway
../mvnw clean package
cd ..

echo "All services built successfully!"
