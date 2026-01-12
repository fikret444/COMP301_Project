FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar car-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "car-service.jar"]

