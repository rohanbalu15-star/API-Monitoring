# Quick Setup Guide

This guide will get you up and running in 5 minutes.

## Prerequisites

Install:
- Java 17+ ([Download](https://adoptium.net/))
- Node.js 18+ ([Download](https://nodejs.org/))
- Docker Desktop ([Download](https://www.docker.com/products/docker-desktop))

## Step 1: Start MongoDB Databases

```bash
# Start both MongoDB instances using Docker Compose
docker-compose up -d

# Verify both are running
docker ps
```

You should see:
- `mongodb-logs` on port 27017
- `mongodb-metadata` on port 27018

## Step 2: Start Collector Service

```bash
# Navigate to collector service
cd collector-service

# Run the service (will download dependencies first time)
./gradlew bootRun
```

Wait for the message: `Started CollectorServiceApplication`

## Step 3: Create a User Account

Open a new terminal:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "email": "admin@example.com"
  }'
```

You should see: `{"message":"User registered successfully"}`

## Step 4: Start the Dashboard

```bash
# Navigate to dashboard
cd dashboard

# Install dependencies
npm install

# Start development server
npm run dev
```

Wait for: `Ready on http://localhost:3000`

## Step 5: Login to Dashboard

1. Open your browser: http://localhost:3000
2. Login with:
   - Username: `admin`
   - Password: `admin123`

You're in! The dashboard will be empty initially.

## Step 6: Generate Test Data

Send some test logs:

```bash
# Successful API call
curl -X POST http://localhost:8080/api/logs \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "test-service",
    "endpoint": "/api/users",
    "method": "GET",
    "requestSize": 1024,
    "responseSize": 2048,
    "statusCode": 200,
    "timestamp": "2024-01-15T10:30:00Z",
    "latencyMs": 150,
    "eventType": "api-call"
  }'

# Slow API (generates alert)
curl -X POST http://localhost:8080/api/logs \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "test-service",
    "endpoint": "/api/reports",
    "method": "POST",
    "requestSize": 512,
    "responseSize": 4096,
    "statusCode": 200,
    "timestamp": "2024-01-15T10:31:00Z",
    "latencyMs": 750,
    "eventType": "api-call"
  }'

# Broken API (generates alert)
curl -X POST http://localhost:8080/api/logs \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "test-service",
    "endpoint": "/api/payments",
    "method": "POST",
    "requestSize": 2048,
    "responseSize": 0,
    "statusCode": 500,
    "timestamp": "2024-01-15T10:32:00Z",
    "latencyMs": 200,
    "eventType": "api-call"
  }'

# Rate limit hit
curl -X POST http://localhost:8080/api/logs \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "test-service",
    "endpoint": "/api/search",
    "method": "GET",
    "requestSize": 256,
    "responseSize": 1024,
    "statusCode": 200,
    "timestamp": "2024-01-15T10:33:00Z",
    "latencyMs": 50,
    "eventType": "rate-limit-hit"
  }'
```

## Step 7: Explore the Dashboard

Refresh the dashboard (http://localhost:3000/dashboard) and you'll see:

1. **Overview Tab**:
   - Stats cards showing counts
   - Top slow endpoints
   - Recent alerts

2. **Logs Tab**:
   - All API logs in a table
   - Apply filters to find specific logs

3. **Alerts Tab**:
   - All generated alerts
   - Color-coded by severity

4. **Incidents Tab**:
   - Open incidents (slow/broken APIs)
   - Click "Resolve" to mark as fixed

## Integrate into Your Microservice

To add monitoring to your own Spring Boot service:

1. **Copy the tracking client JAR** to your service's `libs/` folder

2. **Add dependency** in `build.gradle.kts`:
```kotlin
dependencies {
    implementation(files("libs/api-tracking-client-1.0.0.jar"))
}
```

3. **Configure** in `application.yaml`:
```yaml
monitoring:
  collectorUrl: http://localhost:8080/api/logs
  rateLimit:
    service: my-service-name
    limit: 100
```

4. **Restart your service** - all APIs are now automatically tracked!

## Common Issues

**MongoDB won't start:**
```bash
# Check if ports are in use
lsof -i :27017
lsof -i :27018

# Stop and restart
docker-compose down
docker-compose up -d
```

**Collector service won't start:**
- Make sure MongoDB is running first
- Check Java version: `java -version` (should be 17+)
- Look at logs for specific errors

**Dashboard shows "Network Error":**
- Verify collector service is running on port 8080
- Check CORS settings if accessing from different origin

**Can't login:**
- Make sure you registered a user (Step 3)
- Try registering a new user if password forgotten
- Check collector service logs for authentication errors

## Next Steps

- Read the main [README.md](README.md) for detailed architecture
- Explore the API endpoints
- Customize rate limits for different services
- Integrate into your own microservices

## Stopping Everything

```bash
# Stop collector service: Ctrl+C in its terminal

# Stop dashboard: Ctrl+C in its terminal

# Stop MongoDB
docker-compose down

# Stop and remove volumes (deletes all data)
docker-compose down -v
```

That's it! You now have a fully functional API monitoring platform.
