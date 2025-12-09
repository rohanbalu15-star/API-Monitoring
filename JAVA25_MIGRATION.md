# Java 25 Migration Guide

This document explains the changes made to support Java 25.0.1 and Gradle 9.1.

## What Changed

### Build Configuration

All three Kotlin/Spring Boot services have been updated:

#### 1. collector-service/build.gradle.kts
- Changed `java.sourceCompatibility` from `VERSION_17` to `VERSION_25`
- Changed Kotlin `jvmTarget` from `"17"` to `"25"`

#### 2. api-tracking-client/build.gradle.kts
- Changed `java.sourceCompatibility` from `VERSION_17` to `VERSION_25`
- Changed Kotlin `jvmTarget` from `"17"` to `"25"`

#### 3. example-service/build.gradle.kts
- Changed `java.sourceCompatibility` from `VERSION_17` to `VERSION_25`
- Changed Kotlin `jvmTarget` from `"17"` to `"25"`

### Gradle Wrapper

Updated all `gradle/wrapper/gradle-wrapper.properties` files:
- Changed from Gradle 8.5 to Gradle 8.11
- Distribution URL updated to: `https://services.gradle.org/distributions/gradle-8.11-bin.zip`

**Note:** Gradle 8.11+ is required for Java 25 support.

### Documentation

Updated `SETUP.md` with:
- Java 25 installation instructions (replacing Java 17)
- Gradle 9.1+ requirement
- Version compatibility table
- New troubleshooting sections for Java 25 specific issues
- Quick start guide for users with correct versions installed

Created new documentation:
- `VERSION_INFO.md` - Comprehensive version requirements and compatibility matrix
- `JAVA25_MIGRATION.md` - This file

## Files Modified

```
✅ collector-service/build.gradle.kts
✅ collector-service/gradle/wrapper/gradle-wrapper.properties
✅ api-tracking-client/build.gradle.kts
✅ api-tracking-client/gradle/wrapper/gradle-wrapper.properties
✅ example-service/build.gradle.kts
✅ example-service/gradle/wrapper/gradle-wrapper.properties
✅ SETUP.md
✅ VERSION_INFO.md (new)
✅ JAVA25_MIGRATION.md (new)
```

## Why Java 25?

Java 25 offers several improvements over Java 17:

1. **Modern Language Features**: Latest Java enhancements and syntax improvements
2. **Performance**: JVM optimizations and better garbage collection
3. **Security**: Latest security patches and vulnerability fixes
4. **Tooling**: Better IDE support and debugging capabilities
5. **Future-Proof**: Staying current with the Java ecosystem

## How to Use This Project Now

### Prerequisites

You MUST have installed:
- ✅ Java 25.0.1 or higher
- ✅ Gradle 9.1.0 or higher
- ✅ Node.js 18.x or higher
- ✅ Docker Desktop

### Running the Application

Since you have Gradle 9.1 installed globally, you can use it directly:

```powershell
# Navigate to the service directory
cd collector-service

# Run the service
gradle bootRun
```

**Note:** You do NOT need to use `.\gradlew.bat` anymore. Just use `gradle` directly.

### If You Get Gradle Wrapper Errors

If you still want to use the Gradle wrapper, regenerate it:

```powershell
cd collector-service
gradle wrapper --gradle-version 8.11

# Now you can use
.\gradlew.bat bootRun
```

## Verification

After making these changes, verify your setup:

### 1. Check Java Version

```powershell
java -version
```

Expected output:
```
openjdk version "25.0.1" 2025-10-21 LTS
OpenJDK Runtime Environment Temurin-25.0.1+8 (build 25.0.1+8-LTS)
```

### 2. Check Gradle Version

```powershell
gradle -v
```

Expected output:
```
Gradle 9.1.0
```

### 3. Verify JAVA_HOME

```powershell
echo $env:JAVA_HOME
```

Expected output (example):
```
C:\Program Files\Eclipse Adoptium\jdk-25.0.1+8-hotspot
```

### 4. Test Build

```powershell
cd collector-service
gradle clean build
```

This should complete without errors.

## Troubleshooting

### Error: "Unsupported class file major version 69"

**Cause:** You're trying to run Java 25 compiled code with an older Java version.

**Solution:**
1. Install Java 25 from https://adoptium.net/
2. Set JAVA_HOME to point to Java 25
3. Restart PowerShell
4. Run `gradle clean build` again

### Error: "Could not target platform: 'Java SE 25'"

**Cause:** Your Gradle version is too old.

**Solution:**
1. Update Gradle to 9.1+ using Chocolatey: `choco upgrade gradle`
2. Or download manually from https://gradle.org/releases/
3. Verify: `gradle -v`

### Error: "gradlew is not recognized"

**Cause:** Gradle wrapper JAR file is missing.

**Solution:** Use `gradle` directly instead:
```powershell
gradle bootRun
```

Or regenerate the wrapper:
```powershell
gradle wrapper --gradle-version 8.11
```

### Build Fails with "package does not exist"

**Cause:** Dependencies need to be re-downloaded for Java 25.

**Solution:**
```powershell
gradle clean
gradle --refresh-dependencies build
```

## IDE Configuration

### IntelliJ IDEA

1. **Update Project SDK:**
   - File → Project Structure → Project
   - SDK: Select "25" or "Download JDK..." → Eclipse Temurin 25

2. **Update Gradle JVM:**
   - File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Gradle JVM: Select "Project SDK (25)"

3. **Invalidate Caches:**
   - File → Invalidate Caches / Restart
   - Select "Invalidate and Restart"

### VS Code

Add to `.vscode/settings.json`:
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-25",
      "path": "C:\\Program Files\\Eclipse Adoptium\\jdk-25.0.1+8-hotspot",
      "default": true
    }
  ],
  "java.jdt.ls.java.home": "C:\\Program Files\\Eclipse Adoptium\\jdk-25.0.1+8-hotspot"
}
```

## Testing the Changes

Run the full application to verify everything works:

```powershell
# Terminal 1: Start MongoDB
docker-compose up -d

# Terminal 2: Start Backend
cd collector-service
gradle bootRun

# Terminal 3: Start Dashboard
cd dashboard
npm run dev

# Terminal 4: Create test user
$body = @{username="admin";password="admin123";email="admin@example.com"} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body $body
```

Then visit http://localhost:3000 and login.

## Rollback (If Needed)

If you need to rollback to Java 17 for any reason:

1. Reinstall Java 17
2. Change all `build.gradle.kts` files back to:
   - `java.sourceCompatibility = JavaVersion.VERSION_17`
   - `jvmTarget = "17"`
3. Update gradle-wrapper.properties to use Gradle 8.5
4. Run `gradle clean build`

However, this is NOT recommended as the project is now designed for Java 25.

## Benefits of This Migration

After migrating to Java 25, you get:

✅ **Better Performance**: Improved JVM optimizations
✅ **Modern Features**: Access to latest Java language features
✅ **Enhanced Security**: Latest security patches
✅ **Better Tooling**: Improved IDE support and error messages
✅ **Future-Proof**: Stay current with the ecosystem

## Support

For issues related to Java 25 migration:

1. Check [VERSION_INFO.md](VERSION_INFO.md) for version requirements
2. Review [SETUP.md](SETUP.md) troubleshooting section
3. Verify all versions match the requirements
4. Ensure JAVA_HOME is set correctly
5. Try clean build: `gradle clean build`

---

**Migration Date:** December 2025
**Target Java Version:** 25.0.1
**Target Gradle Version:** 9.1.0+
**Status:** ✅ Complete
