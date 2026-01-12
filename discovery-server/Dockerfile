FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar discovery-server.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "discovery-server.jar"]

