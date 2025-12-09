# Version Requirements and Compatibility

This document outlines the specific version requirements for the API Monitoring Platform.

## Summary

**Java 25.0.1** is **REQUIRED** for this project. The project will NOT compile or run with Java 17 or lower versions.

## Required Versions

### Core Requirements

- **Java**: 25.0.1 (LTS)
  - Download: https://adoptium.net/
  - Minimum: 25.x
  - Status: ✅ Required

- **Gradle**: 9.1.0+
  - Download: https://gradle.org/releases/
  - Minimum: 8.10 (for Java 25 support)
  - Wrapper Version: 8.11
  - Status: ✅ Required

- **Kotlin**: 1.9.22
  - Status: ✅ Bundled with project
  - JVM Target: 25

- **Spring Boot**: 3.2.2
  - Status: ✅ Bundled with project

### Frontend Requirements

- **Node.js**: 18.x or higher
  - Download: https://nodejs.org/
  - Status: ✅ Required for dashboard

### Infrastructure

- **Docker Desktop**: Latest
  - Download: https://www.docker.com/products/docker-desktop
  - Status: ✅ Required for MongoDB

- **MongoDB**: 5.0+ (via Docker)
  - Status: ✅ Managed by Docker Compose

## Why Java 25?

This project has been configured to use Java 25 for the following reasons:

1. **Modern Language Features**: Access to the latest Java language enhancements
2. **Performance Improvements**: Better JVM performance and optimizations
3. **Security Updates**: Latest security patches and improvements
4. **Future-Proofing**: Staying current with Java ecosystem

## Compatibility Matrix

| Component | Java 17 | Java 21 | Java 25 |
|-----------|---------|---------|---------|
| Collector Service | ❌ | ❌ | ✅ |
| API Tracking Client | ❌ | ❌ | ✅ |
| Example Service | ❌ | ❌ | ✅ |
| Dashboard | N/A | N/A | N/A |

## Gradle Configuration

All Gradle build files (`build.gradle.kts`) are configured with:

```kotlin
java.sourceCompatibility = JavaVersion.VERSION_25

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "25"
    }
}
```

## Gradle Wrapper

The Gradle wrapper is configured to use Gradle 8.11, which supports Java 25:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.11-bin.zip
```

## Checking Your Versions

### Check Java Version

```powershell
java -version
```

Expected output:
```
openjdk version "25.0.1" 2025-10-21 LTS
OpenJDK Runtime Environment Temurin-25.0.1+8 (build 25.0.1+8-LTS)
OpenJDK 64-Bit Server VM Temurin-25.0.1+8 (build 25.0.1+8-LTS, mixed mode, sharing)
```

### Check Gradle Version

```powershell
gradle -v
```

Expected output (minimum):
```
Gradle 9.1.0
```

### Check Node Version

```powershell
node -v
```

Expected output (minimum):
```
v18.x.x
```

## Troubleshooting Version Issues

### "Unsupported class file major version 69"

This error means you're trying to run Java 25 compiled code with an older Java version.

**Solution:** Install Java 25 and set JAVA_HOME correctly.

### "Could not target platform: 'Java SE 25'"

This error means your Gradle version is too old.

**Solution:** Update to Gradle 8.10 or higher.

### Gradle wrapper missing

If `.\gradlew.bat` doesn't work, use `gradle` directly:

```powershell
# Instead of
.\gradlew.bat bootRun

# Use
gradle bootRun
```

Or regenerate the wrapper:
```powershell
gradle wrapper --gradle-version 8.11
```

## Migration from Java 17

If you previously used Java 17 with this project, you need to:

1. **Uninstall Java 17** (optional but recommended)
2. **Install Java 25** from https://adoptium.net/
3. **Update JAVA_HOME** environment variable
4. **Update Gradle** to version 9.1+ (if not already)
5. **Clean and rebuild**:
   ```powershell
   gradle clean
   gradle build
   ```

## Development Environment Setup

### IntelliJ IDEA

1. Open Settings (Ctrl+Alt+S)
2. Go to: Build, Execution, Deployment → Build Tools → Gradle
3. Set "Gradle JVM" to: Java 25
4. Go to: Project Structure → Project
5. Set "SDK" to: 25 (java version "25.0.1")
6. Set "Language level" to: SDK default

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
  ]
}
```

## CI/CD Considerations

If you're using CI/CD pipelines, ensure:

1. Java 25 is installed in the build environment
2. Gradle 8.11+ is available
3. JAVA_HOME points to Java 25 installation
4. Build scripts use correct Java version

## Support

If you encounter version-related issues:

1. Verify all versions match requirements
2. Check environment variables (JAVA_HOME, PATH)
3. Restart your IDE and terminal
4. Clean and rebuild the project
5. Check the [Troubleshooting](SETUP.md#troubleshooting) section

## Updates

This project will be maintained to work with:
- Current Java LTS version (25 as of 2025)
- Latest stable Gradle version
- Latest stable Spring Boot version

---

**Last Updated:** December 2025
**Project Version:** 1.0.0
