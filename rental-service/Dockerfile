# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /build/target/*.jar rental-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "rental-service.jar"]

