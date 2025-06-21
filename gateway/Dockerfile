FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/gateway-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]