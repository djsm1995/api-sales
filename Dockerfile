# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

FROM jenkins/jenkins:lts
# Cambiamos a root temporalmente para instalar herramientas
USER root
RUN apt-get update && apt-get install -y docker.io && rm -rf /var/lib/apt/lists/*
# Volvemos al usuario jenkins por seguridad
USER jenkins

# Actuator port
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]