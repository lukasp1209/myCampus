# Notifications Service README
The Notifications Service is a Spring Boot Java module in the University Management Platform monorepo. It delivers notifications (e.g., schedules, exams, attendance, notes) via REST API and WebSocket, using PostgreSQL for storage.

## Prerequisites
- Docker & Docker Compose (for containers).
- Java 17+ and Maven 3.8+ (for local dev).

## Docker Compose Setup
1. Go to myCampus monorepo
2. Run
```
docker-compose up --build notifications-service postgres 
```
3. Stop
```
docker-compose down
```

## Local Development Setup
1. Set Up PostgresSQL
```
docker-compose up --build postgres pgadmin 
```
2. Create notifications_db database
3. Go to Service Directory
```
cd notifications-service
```
4.Run 
```
mvn clean install
mvn spring-boot:run
```
- REST API: http://localhost:8080
- WebSocket: ws://localhost:8080/ws
