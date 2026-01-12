#!/bin/bash

echo "Building all microservices..."

# Build Discovery Server
echo "Building Discovery Server..."
cd discovery-server
./mvnw clean package -DskipTests
cd ..

# Build API Gateway
echo "Building API Gateway..."
cd api-gateway
./mvnw clean package -DskipTests
cd ..

# Build Config Server
echo "Building Config Server..."
cd config-server
./mvnw clean package -DskipTests
cd ..

# Build User Service
echo "Building User Service..."
cd user-service
./mvnw clean package -DskipTests
cd ..

# Build Car Service
echo "Building Car Service..."
cd car-service
./mvnw clean package -DskipTests
cd ..

# Build Rental Service
echo "Building Rental Service..."
cd rental-service
./mvnw clean package -DskipTests
cd ..

# Build Payment Service
echo "Building Payment Service..."
cd payment-service
./mvnw clean package -DskipTests
cd ..

echo "All services built successfully!"
echo "You can now run 'docker-compose up --build' to start all services."

