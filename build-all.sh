#!/bin/bash

echo "==================================="
echo "Building all microservices"
echo "==================================="

echo "Building Order Service..."
cd order-service
mvn clean package "$@"
if [ $? -ne 0 ]; then
    echo "Order Service build failed!"
    exit 1
fi
cd ..

echo "Building Inventory Service..."
cd inventory-service
mvn clean package "$@"
if [ $? -ne 0 ]; then
    echo "Inventory Service build failed!"
    exit 1
fi
cd ..

echo "Building Billing Service..."
cd billing-service
mvn clean package "$@"
if [ $? -ne 0 ]; then
    echo "Billing Service build failed!"
    exit 1
fi
cd ..

echo "Building API Gateway..."
cd api-gateway
mvn clean package "$@"
if [ $? -ne 0 ]; then
    echo "API Gateway build failed!"
    exit 1
fi
cd ..

echo "==================================="
echo "All services built successfully!"
echo "==================================="
