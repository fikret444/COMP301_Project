@echo off
echo Building all microservices...

echo Building Discovery Server...
cd discovery-server
call mvnw.cmd clean package -DskipTests
cd ..

echo Building API Gateway...
cd api-gateway
call mvnw.cmd clean package -DskipTests
cd ..

echo Building Config Server...
cd config-server
call mvnw.cmd clean package -DskipTests
cd ..

echo Building User Service...
cd user-service
call mvnw.cmd clean package -DskipTests
cd ..

echo Building Car Service...
cd car-service
call mvnw.cmd clean package -DskipTests
cd ..

echo Building Rental Service...
cd rental-service
call mvnw.cmd clean package -DskipTests
cd ..

echo Building Payment Service...
cd payment-service
call mvnw.cmd clean package -DskipTests
cd ..

echo All services built successfully!
echo You can now run 'docker-compose up --build' to start all services.
pause

