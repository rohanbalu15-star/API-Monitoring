# Windows Setup Guide

This guide explains how to run the API Monitoring Platform on Windows.

## Issue: Gradle Wrapper Missing

The Gradle wrapper JAR file is not included in the repository due to its size. You have two options:

## Option 1: Use Pre-installed Gradle (Recommended for Quick Start)

If you have Gradle installed on your system:

### 1. Install Gradle

Download and install Gradle from: https://gradle.org/install/

Or using Chocolatey:
```powershell
choco install gradle
```

Or using Scoop:
```powershell
scoop install gradle
```

### 2. Run the Collector Service

```powershell
cd collector-service
gradle bootRun
```

### 3. Run the Example Service (Optional)

```powershell
cd example-service
gradle bootRun
```

## Option 2: Generate Gradle Wrapper

If you want to use the Gradle wrapper:

### 1. Navigate to the collector-service directory

```powershell
cd collector-service
```

### 2. Generate the wrapper (requires Gradle to be installed)

```powershell
gradle wrapper --gradle-version 8.5
```

### 3. Now you can use the wrapper

```powershell
.\gradlew.bat bootRun
```

### 4. Repeat for other services

Do the same for `api-tracking-client` and `example-service` if needed.

## Option 3: Use IntelliJ IDEA or Eclipse

### IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA
2. Click "Open" and select the `collector-service` folder
3. IntelliJ will automatically detect it's a Gradle project
4. Wait for dependencies to download
5. Find `CollectorServiceApplication.kt`
6. Right-click and select "Run 'CollectorServiceApplication'"

### Eclipse

1. Install the Spring Tools 4 plugin
2. Import as "Existing Gradle Project"
3. Select the `collector-service` folder
4. Right-click the project → Run As → Spring Boot App

## Complete Setup Steps for Windows

### Prerequisites

1. **Install Java 17+**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Install Node.js 18+**
   - Download from: https://nodejs.org/
   - Verify installation: `node -v`

3. **Install Docker Desktop**
   - Download from: https://www.docker.com/products/docker-desktop
   - Start Docker Desktop

4. **Install Gradle** (for Option 1 or 2 above)
   - Download from: https://gradle.org/install/
   - Or use Chocolatey: `choco install gradle`

### Step-by-Step Setup

#### 1. Start MongoDB

```powershell
# Navigate to project root
cd C:\Users\My.PC\OneDrive\Desktop\APImonitoring\API-Monitoring

# Start MongoDB containers
docker-compose up -d

# Verify containers are running
docker ps
```

You should see two MongoDB containers:
- `mongodb-logs` on port 27017
- `mongodb-metadata` on port 27018

#### 2. Start Collector Service

**Using Gradle directly:**
```powershell
cd collector-service
gradle bootRun
```

**Or generate wrapper first:**
```powershell
cd collector-service
gradle wrapper --gradle-version 8.5
.\gradlew.bat bootRun
```

Wait for the message: `Started CollectorServiceApplication`

#### 3. Create a User

Open a new PowerShell window:

```powershell
curl.exe -X POST http://localhost:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{\"username\":\"admin\",\"password\":\"admin123\",\"email\":\"admin@example.com\"}'
```

Or use Invoke-RestMethod:
```powershell
$body = @{
    username = "admin"
    password = "admin123"
    email = "admin@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

#### 4. Start Dashboard

Open another PowerShell window:

```powershell
cd dashboard
npm install
npm run dev
```

Wait for: `Ready on http://localhost:3000`

#### 5. Access Dashboard

1. Open browser: http://localhost:3000
2. Login with:
   - Username: `admin`
   - Password: `admin123`

#### 6. Generate Test Data (Optional)

Send a test log:

```powershell
$body = @{
    serviceName = "test-service"
    endpoint = "/api/users"
    method = "GET"
    requestSize = 1024
    responseSize = 2048
    statusCode = 200
    timestamp = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
    latencyMs = 150
    eventType = "api-call"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/logs" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

## Troubleshooting

### Error: "gradlew is not recognized"

**Solution**: Use `.\gradlew.bat` instead of `./gradlew` on Windows:
```powershell
.\gradlew.bat bootRun
```

### Error: "JAVA_HOME is not set"

**Solution**: Set JAVA_HOME environment variable:
```powershell
# Find Java installation
where java

# Set JAVA_HOME (adjust path to your Java installation)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.8.7-hotspot"
```

To set permanently:
1. Open System Properties → Advanced → Environment Variables
2. Add new System Variable:
   - Name: `JAVA_HOME`
   - Value: `C:\Program Files\Eclipse Adoptium\jdk-17.0.8.7-hotspot` (your Java path)
3. Restart PowerShell

### Error: "Docker daemon is not running"

**Solution**: Start Docker Desktop from the Start menu

### Error: "Port already in use"

**Solution**: Check what's using the port:
```powershell
# Check port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### Error: "MongoDB connection refused"

**Solution**: Ensure Docker containers are running:
```powershell
docker ps
```

If not running, start them:
```powershell
docker-compose up -d
```

## Quick Commands Reference

### Start Everything

```powershell
# Terminal 1 - MongoDB
docker-compose up -d

# Terminal 2 - Collector Service
cd collector-service
gradle bootRun

# Terminal 3 - Dashboard
cd dashboard
npm run dev
```

### Stop Everything

```powershell
# Stop collector service: Ctrl+C in its terminal

# Stop dashboard: Ctrl+C in its terminal

# Stop MongoDB
docker-compose down
```

## Using IntelliJ IDEA (Easiest Method)

This is the recommended approach for Windows users:

1. **Install IntelliJ IDEA Community Edition** (free)
   - Download from: https://www.jetbrains.com/idea/download/

2. **Open the Project**
   - Open IntelliJ IDEA
   - File → Open → Select `collector-service` folder
   - Wait for Gradle sync to complete

3. **Run the Application**
   - Navigate to: `src/main/kotlin/com/monitoring/collector/CollectorServiceApplication.kt`
   - Click the green play button next to the main function
   - Or right-click → Run 'CollectorServiceApplication'

4. **Run the Dashboard**
   - Open terminal in IntelliJ (Alt+F12)
   - Navigate to dashboard folder
   - Run `npm install` then `npm run dev`

This method handles all Gradle configuration automatically!

## Next Steps

After successful setup:
1. Login to dashboard at http://localhost:3000
2. Start the example-service to generate test data
3. Explore the monitoring features
4. Integrate the tracking client into your own microservices

For detailed architecture and API documentation, see [README.md](README.md)
