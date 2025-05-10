# Build stage
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the JAR file using the exact name from your target folder
COPY --from=builder /app/target/schedule-service-*.jar ./app.jar
# Ensure the JVM uses UTF-8 encoding
ENV LANG C.UTF-8
EXPOSE 8081
# Add optimized JVM flags for containerized environment
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]