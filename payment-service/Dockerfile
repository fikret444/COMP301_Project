FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar payment-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "payment-service.jar"]

