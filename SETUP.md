# Complete Setup Guide for Windows

This comprehensive guide will walk you through everything from cloning the repository to using the dashboard. Designed specifically for Windows users.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Cloning the Repository](#cloning-the-repository)
3. [Installing Required Software](#installing-required-software)
4. [Running the Application](#running-the-application)
5. [Creating Your First User](#creating-your-first-user)
6. [Using the Dashboard](#using-the-dashboard)
7. [Generating Test Data](#generating-test-data)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before starting, ensure you have:
- Windows 10 or 11
- Administrator access (for installing software)
- Internet connection
- At least 4GB of free RAM
- 5GB of free disk space

## Cloning the Repository

### Step 1: Install Git (if not already installed)

1. Download Git for Windows: https://git-scm.com/download/win
2. Run the installer with default settings
3. Verify installation by opening PowerShell and typing:
   ```powershell
   git --version
   ```

### Step 2: Clone the Repository

Open PowerShell and navigate to where you want to store the project:

```powershell
# Navigate to your desired location (example: Desktop)
cd $HOME\Desktop

# Clone the repository (replace with your actual repository URL)
git clone https://github.com/your-username/API-Monitoring.git

# Navigate into the project
cd API-Monitoring
```

If you downloaded the project as a ZIP file instead:
1. Extract the ZIP file to your desired location
2. Open PowerShell and navigate to the extracted folder:
   ```powershell
   cd "C:\Users\YourUsername\Desktop\API-Monitoring"
   ```

---

## Installing Required Software

You'll need to install 4 pieces of software. Let's go through each one step by step.

### 1. Install Java 17 (Required for Backend)

**Step 1:** Download Java
- Visit: https://adoptium.net/
- Click "Download" for the latest Java 17 (LTS)
- Choose "Windows x64" installer (.msi file)

**Step 2:** Install Java
- Run the downloaded installer
- Click "Next" → "Next" → "Install"
- Wait for installation to complete

**Step 3:** Verify Installation
```powershell
java -version
```

You should see something like: `openjdk version "17.0.x"`

**Step 4:** Set JAVA_HOME (Important!)
1. Press `Win + R`, type `sysdm.cpl`, press Enter
2. Go to "Advanced" tab → Click "Environment Variables"
3. Under "System variables", click "New"
4. Variable name: `JAVA_HOME`
5. Variable value: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot` (your actual path)
6. Click OK on all windows
7. **Restart PowerShell** for changes to take effect

### 2. Install Node.js (Required for Dashboard)

**Step 1:** Download Node.js
- Visit: https://nodejs.org/
- Download the LTS version (18.x or higher)
- Choose "Windows Installer (.msi)"

**Step 2:** Install Node.js
- Run the installer
- Click "Next" → Accept license → "Next"
- Use default installation path → "Next"
- Check "Automatically install necessary tools" → "Next"
- Click "Install"

**Step 3:** Verify Installation
```powershell
node -v
npm -v
```

You should see version numbers for both.

### 3. Install Docker Desktop (Required for MongoDB)

**Step 1:** Download Docker Desktop
- Visit: https://www.docker.com/products/docker-desktop
- Click "Download for Windows"
- Choose the appropriate version for your system

**Step 2:** Install Docker Desktop
- Run the installer
- Follow the installation wizard
- When prompted, ensure "Use WSL 2" is selected
- Restart your computer when installation completes

**Step 3:** Start Docker Desktop
- Open Docker Desktop from Start Menu
- Wait for it to start (green icon in system tray)
- You might need to accept terms on first launch

**Step 4:** Verify Installation
```powershell
docker --version
docker ps
```

You should see version info and an empty container list.

### 4. Install Gradle (Required for Building Backend)

**Option A: Using Chocolatey (Recommended)**

First, install Chocolatey if you don't have it:
1. Open PowerShell as Administrator
2. Run:
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   ```

Then install Gradle:
```powershell
choco install gradle
```

**Option B: Manual Installation**

1. Download from: https://gradle.org/install/
2. Extract to `C:\Gradle`
3. Add to PATH:
   - System Properties → Environment Variables
   - Edit "Path" in System variables
   - Add: `C:\Gradle\gradle-8.5\bin`
4. Restart PowerShell

**Verify Installation:**
```powershell
gradle -v
```

---

## Running the Application

Now that everything is installed, let's start the application!

### Step 1: Start MongoDB Databases

Open PowerShell in your project directory:

```powershell
# Make sure you're in the project root
cd "C:\Users\YourUsername\Desktop\API-Monitoring"

# Start MongoDB containers
docker-compose up -d

# Verify both containers are running
docker ps
```

**Expected Output:**
You should see two containers running:
- `mongodb-logs` on port 27017
- `mongodb-metadata` on port 27018

**Troubleshooting:** If Docker fails to start:
1. Make sure Docker Desktop is running (check system tray)
2. Try: `docker-compose down` then `docker-compose up -d`

### Step 2: Start the Collector Service (Backend)

**You have two options:**

#### Option A: Using Gradle (Simpler)

Open a **new PowerShell window** and run:

```powershell
# Navigate to collector-service folder
cd "C:\Users\YourUsername\Desktop\API-Monitoring\collector-service"

# Run the service (first run will download dependencies - takes 2-5 minutes)
gradle bootRun
```

#### Option B: Using IntelliJ IDEA (Easiest for Development)

1. **Download IntelliJ IDEA Community Edition** (free)
   - Visit: https://www.jetbrains.com/idea/download/
   - Download and install "Community Edition"

2. **Open the Project**
   - Launch IntelliJ IDEA
   - Click "Open"
   - Navigate to and select the `collector-service` folder
   - Click OK

3. **Wait for Initial Setup**
   - IntelliJ will detect it's a Gradle project
   - Wait for "Indexing" and "Building" to complete (5-10 minutes first time)
   - You'll see progress in the bottom right

4. **Run the Application**
   - In the Project panel (left side), navigate to:
     `src/main/kotlin/com/monitoring/collector/CollectorServiceApplication.kt`
   - Right-click on the file
   - Select "Run 'CollectorServiceApplication'"

   OR

   - Click the green play button (▶) next to the `main` function

**Wait for this message in the console:**
```
Started CollectorServiceApplication in X.XXX seconds
```

**Important:** Keep this window/terminal open! The service needs to keep running.

### Step 3: Start the Dashboard (Frontend)

Open a **new PowerShell window** (keep the previous one running):

```powershell
# Navigate to dashboard folder
cd "C:\Users\YourUsername\Desktop\API-Monitoring\dashboard"

# Install dependencies (first time only - takes 2-3 minutes)
npm install

# Start the development server
npm run dev
```

**Wait for this message:**
```
Ready on http://localhost:3000
```

**Important:** Keep this window open too! You now have 2 windows running:
1. Collector Service (Backend) on port 8080
2. Dashboard (Frontend) on port 3000

---

## Creating Your First User

Before you can login, you need to create a user account.

Open a **new PowerShell window** (3rd window):

```powershell
# Using PowerShell's Invoke-RestMethod
$body = @{
    username = "admin"
    password = "admin123"
    email = "admin@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body $body
```

**Expected Response:**
```
message
-------
User registered successfully
```

**Alternative:** If you have `curl` installed:
```powershell
curl.exe -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{\"username\":\"admin\",\"password\":\"admin123\",\"email\":\"admin@example.com\"}'
```

---

## Using the Dashboard

### Step 1: Open the Dashboard

1. Open your web browser (Chrome, Edge, or Firefox)
2. Go to: http://localhost:3000

### Step 2: Login

You'll see a login page:
- **Username:** `admin`
- **Password:** `admin123`
- Click "Sign in"

### Step 3: Explore the Dashboard

After logging in, you'll see the main dashboard with 4 tabs:

**1. Overview Tab (Default View)**
- Shows statistics cards at the top
- Top 5 slow endpoints
- Recent alerts

**2. Logs Tab**
- Complete table of all API requests
- Use the filters panel on the left to filter by:
  - Service name
  - Endpoint
  - Slow APIs (>500ms)
  - Broken APIs (5xx errors)
  - Rate limit hits

**3. Alerts Tab**
- All system alerts
- Color-coded by severity:
  - Red: Critical (broken APIs)
  - Yellow: Warning (slow APIs, rate limits)

**4. Incidents Tab**
- List of all incidents (slow/broken endpoints)
- You can mark incidents as "Resolved"
- Shows who resolved each incident

---

## Generating Test Data

The dashboard will be empty initially. Let's generate some test data!

### Method 1: Using PowerShell (Recommended)

Keep your PowerShell window open and run these commands:

**1. Normal API Call (Success)**
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

Invoke-RestMethod -Uri "http://localhost:8080/api/logs" -Method POST -ContentType "application/json" -Body $body
```

**2. Slow API Call (Generates Alert)**
```powershell
$body = @{
    serviceName = "test-service"
    endpoint = "/api/reports"
    method = "POST"
    requestSize = 512
    responseSize = 4096
    statusCode = 200
    timestamp = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
    latencyMs = 750
    eventType = "api-call"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/logs" -Method POST -ContentType "application/json" -Body $body
```

**3. Broken API Call (Generates Critical Alert)**
```powershell
$body = @{
    serviceName = "test-service"
    endpoint = "/api/payments"
    method = "POST"
    requestSize = 2048
    responseSize = 0
    statusCode = 500
    timestamp = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
    latencyMs = 200
    eventType = "api-call"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/logs" -Method POST -ContentType "application/json" -Body $body
```

**4. Rate Limit Hit**
```powershell
$body = @{
    serviceName = "test-service"
    endpoint = "/api/search"
    method = "GET"
    requestSize = 256
    responseSize = 1024
    statusCode = 200
    timestamp = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
    latencyMs = 50
    eventType = "rate-limit-hit"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/logs" -Method POST -ContentType "application/json" -Body $body
```

### Method 2: Using the Example Service

The project includes a demo service that generates realistic test data.

Open a **new PowerShell window**:

```powershell
# Navigate to example-service
cd "C:\Users\YourUsername\Desktop\API-Monitoring\example-service"

# Run the service
gradle bootRun
```

Now you can call these endpoints to generate automatic tracking:

```powershell
# Normal request
curl.exe http://localhost:8081/api/users

# Slow endpoint (will trigger alert)
curl.exe http://localhost:8081/api/slow-endpoint

# Error endpoint (will trigger critical alert)
curl.exe http://localhost:8081/api/error-endpoint

# Random latency endpoint
curl.exe http://localhost:8081/api/reports
```

### Step 4: View the Data in Dashboard

1. Go back to your browser (http://localhost:3000)
2. Click the "Refresh" button in the top right
3. You should now see:
   - Updated statistics in the Overview tab
   - Logs in the Logs tab
   - Alerts in the Alerts tab
   - Incidents in the Incidents tab

### Step 5: Try the Features

**Filter Logs:**
1. Click on the "Logs" tab
2. Use the filters on the left:
   - Enter "test-service" in Service Name
   - Check "Slow APIs" checkbox
   - Click "Apply Filters"
3. You'll see only slow API calls

**Resolve an Incident:**
1. Click on the "Incidents" tab
2. Find an open incident
3. Click "Resolve" button
4. The status will change to "Resolved"

**View Analytics:**
1. Go to "Overview" tab
2. Check the "Top 5 Slow Endpoints" section
3. See average latency for each endpoint

---

## Troubleshooting

### Error: "gradlew is not recognized"

**Problem:** You're trying to use `./gradlew` on Windows.

**Solution:**
- On Windows, use: `.\gradlew.bat bootRun`
- Or better yet, just use: `gradle bootRun`

### Error: "JAVA_HOME is not set"

**Problem:** Java environment variable is missing.

**Solution:**
1. Find your Java installation:
   ```powershell
   where.exe java
   ```
2. Set JAVA_HOME temporarily:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"
   ```
3. Set permanently:
   - System Properties → Environment Variables
   - Add System Variable: `JAVA_HOME`
   - Value: Path to your Java installation (not the bin folder)
4. Restart PowerShell

### Error: "Docker daemon is not running"

**Problem:** Docker Desktop is not started.

**Solution:**
1. Open Docker Desktop from Start Menu
2. Wait for the whale icon to stop animating in system tray
3. Try your command again

### Error: "Port 8080 is already in use"

**Problem:** Another application is using port 8080.

**Solution:**
1. Find what's using the port:
   ```powershell
   netstat -ano | findstr :8080
   ```
2. Note the PID (last column)
3. Kill the process:
   ```powershell
   taskkill /PID <PID> /F
   ```
4. Or change the port in `collector-service/src/main/resources/application.yaml`:
   ```yaml
   server:
     port: 8081
   ```

### Error: "MongoDB connection refused"

**Problem:** MongoDB containers are not running.

**Solution:**
1. Check if containers are running:
   ```powershell
   docker ps
   ```
2. If not listed, start them:
   ```powershell
   docker-compose up -d
   ```
3. If that fails, restart Docker Desktop

### Error: "Cannot find module" (Dashboard)

**Problem:** Node modules not installed properly.

**Solution:**
```powershell
cd dashboard
rm -r node_modules
rm package-lock.json
npm install
```

### Dashboard shows "Network Error"

**Problem:** Backend is not running or wrong URL.

**Solution:**
1. Verify backend is running at http://localhost:8080
2. Check in browser: http://localhost:8080/api/stats (should show error about auth)
3. Restart collector service

### Login fails with "Invalid credentials"

**Problem:** User was not created properly.

**Solution:**
1. Try registering again with a different username
2. Check collector service console for error messages
3. Verify MongoDB is running

---

## Stopping the Application

When you're done and want to stop everything:

### Stop Services (Keep Data)

1. **Stop Dashboard:**
   - Go to the PowerShell window running the dashboard
   - Press `Ctrl + C`

2. **Stop Collector Service:**
   - Go to the PowerShell window running collector service
   - Press `Ctrl + C`
   - Or in IntelliJ: Click the red stop button (■)

3. **Stop MongoDB:**
   ```powershell
   docker-compose down
   ```

### Stop Everything and Delete Data

If you want to start fresh next time:

```powershell
# Stop MongoDB and delete all data
docker-compose down -v

# The -v flag removes all stored logs and user data
```

---

## Quick Reference

### Starting Everything

```powershell
# Terminal 1: Start MongoDB
docker-compose up -d

# Terminal 2: Start Backend
cd collector-service
gradle bootRun

# Terminal 3: Start Frontend
cd dashboard
npm run dev
```

### Accessing the Application

- **Dashboard:** http://localhost:3000
- **Backend API:** http://localhost:8080
- **Example Service:** http://localhost:8081 (if running)

### Default Login Credentials

- **Username:** admin
- **Password:** admin123

---

## Next Steps

### 1. Integrate with Your Own Microservice

To add monitoring to your Spring Boot service:

1. Build the tracking client:
   ```powershell
   cd api-tracking-client
   gradle build
   ```

2. Copy JAR to your service:
   ```powershell
   cp build/libs/api-tracking-client-1.0.0.jar C:\your-service\libs\
   ```

3. Add dependency in your `build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(files("libs/api-tracking-client-1.0.0.jar"))
   }
   ```

4. Configure in your `application.yaml`:
   ```yaml
   monitoring:
     collectorUrl: http://localhost:8080/api/logs
     rateLimit:
       service: my-service-name
       limit: 100
   ```

5. Restart your service - all APIs are automatically tracked!

### 2. Explore the API

Read the [README.md](README.md) for:
- Detailed architecture
- API endpoint documentation
- Database schemas
- Advanced configuration

### 3. Customize

- Adjust rate limits per service
- Create custom alert rules
- Add more test data
- Integrate with your CI/CD pipeline

---

## Additional Resources

- **Main Documentation:** [README.md](README.md)
- **Architecture Details:** [ARCHITECTURE.md](ARCHITECTURE.md)
- **Project Summary:** [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

---

## Summary

**Congratulations!** You now have a fully functional API Monitoring & Observability Platform running on your Windows machine.

**What you've accomplished:**
- Installed all required software
- Started MongoDB databases
- Ran the backend collector service
- Started the dashboard frontend
- Created a user account
- Generated test data
- Explored the monitoring features

**What you can do now:**
- Monitor API performance across microservices
- Track slow and broken APIs
- Manage rate limiting
- Resolve incidents
- View real-time analytics

**Having issues?** Check the [Troubleshooting](#troubleshooting) section above or see the detailed documentation in README.md.

Enjoy monitoring your APIs!
