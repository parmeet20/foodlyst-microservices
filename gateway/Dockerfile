FROM eclipse-temurin:25-jdk-jammy AS builder
WORKDIR /app
COPY target/gateway-0.0.1-SNAPSHOT.jar app.jar

FROM gcr.io/distroless/java25-debian13
WORKDIR /app
COPY --from=builder /app/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]