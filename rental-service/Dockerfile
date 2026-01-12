FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar rental-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "rental-service.jar"]

