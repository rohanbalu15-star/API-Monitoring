# Technical Architecture Document

## System Architecture

### High-Level Overview

The API Monitoring & Observability Platform follows a distributed microservices architecture with three main layers:

1. **Data Collection Layer**: API Tracking Client embedded in microservices
2. **Processing & Storage Layer**: Central Collector Service with dual MongoDB
3. **Presentation Layer**: Next.js Dashboard

### Component Details

## 1. API Tracking Client

**Technology**: Spring Boot Library (Kotlin)
**Type**: Reusable JAR

### Architecture

```
HTTP Request → HandlerInterceptor → RateLimiter → Original Handler
     ↓                                    ↓
   Track                            Check Limit
     ↓                                    ↓
   Log                             Log if Exceeded
     ↓                                    ↓
Async Send ────────────────────────> Collector Service
```

### Key Components

**ApiTrackingInterceptor**
- Implements Spring's `HandlerInterceptor`
- Captures request/response metadata
- Measures latency with nanosecond precision
- Non-blocking execution

**RateLimiterService**
- Token bucket algorithm
- Atomic operations for thread safety
- Sliding window (1-second intervals)
- Configurable per service

**LogCollectorClient**
- Kotlin Coroutines for async transmission
- Fire-and-forget pattern
- Graceful failure handling
- REST client using RestTemplate

### Integration

Auto-configuration via Spring Boot's `spring.factories`:
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.monitoring.client.config.MonitoringAutoConfiguration
```

## 2. Central Collector Service

**Technology**: Spring Boot (Kotlin)
**Database**: Dual MongoDB (Logs + Metadata)

### Architecture

```
┌─────────────────────────────────────────────────┐
│            Central Collector Service            │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌──────────────┐        ┌─────────────────┐  │
│  │   REST API   │        │  Security Layer │  │
│  │  Controllers │───────▶│  JWT Filter     │  │
│  └──────────────┘        └─────────────────┘  │
│         │                                       │
│         ▼                                       │
│  ┌──────────────────────────────────────────┐ │
│  │         Business Logic Layer             │ │
│  │                                          │ │
│  │  • Log Processing                        │ │
│  │  • Alert Generation                      │ │
│  │  • Incident Management                   │ │
│  │  • Analytics Computation                 │ │
│  └──────────────────────────────────────────┘ │
│         │                                       │
│         ▼                                       │
│  ┌──────────────────────────────────────────┐ │
│  │         Repository Layer                 │ │
│  │                                          │ │
│  │  ┌─────────────┐    ┌────────────────┐ │ │
│  │  │ Logs Repos  │    │ Metadata Repos │ │ │
│  │  └─────────────┘    └────────────────┘ │ │
│  └──────────────────────────────────────────┘ │
│         │                         │            │
└─────────┼─────────────────────────┼────────────┘
          │                         │
          ▼                         ▼
   ┌─────────────┐          ┌─────────────┐
   │  MongoDB    │          │  MongoDB    │
   │  Logs DB    │          │ Metadata DB │
   │  :27017     │          │  :27018     │
   └─────────────┘          └─────────────┘
```

### Dual MongoDB Configuration

**Why Dual Databases?**

1. **Write Performance**: High-frequency logs don't impact metadata operations
2. **Query Performance**: Analytics queries don't slow down incident management
3. **Scalability**: Independent horizontal scaling
4. **Data Lifecycle**: Different retention policies
5. **Backup Strategy**: More frequent backups for metadata

**Implementation**:

```kotlin
@Configuration
class MongoConfig {
    // Primary MongoDB - Logs
    @Bean @Primary
    fun logsMongoTemplate(): MongoTemplate { ... }

    // Secondary MongoDB - Metadata
    @Bean
    fun metadataMongoTemplate(): MongoTemplate { ... }
}
```

### Security Architecture

**JWT Authentication Flow**:

```
1. User Login Request
   ↓
2. Validate Credentials (BCrypt)
   ↓
3. Generate JWT Token (HS256)
   ↓
4. Return Token to Client
   ↓
5. Client includes token in Authorization header
   ↓
6. JwtAuthenticationFilter validates token
   ↓
7. SecurityContext populated with user details
   ↓
8. Request proceeds to controller
```

### Concurrency Control

**Optimistic Locking Pattern**:

```kotlin
// MongoDB Document
@Document
data class Incident(
    @Id val id: String,
    @Version val version: Long,  // Auto-managed by Spring Data
    // ... other fields
)

// Update Operation
fun resolveIncident(id: String) {
    val query = Query(
        Criteria.where("_id").is(id)
            .and("status").is(OPEN)
            .and("version").is(currentVersion)  // Version check
    )
    val update = Update()
        .set("status", RESOLVED)
        .inc("version", 1)  // Increment version

    mongoTemplate.updateFirst(query, update, Incident::class.java)
}
```

**Race Condition Handling**:

```
Timeline:
─────────────────────────────────────────────────►

T1: Dev A reads incident (v=1)
T2: Dev B reads incident (v=1)
T3: Dev A updates → success (v→2)
T4: Dev B updates → fails (v mismatch)
```

### Alert Generation Pipeline

```
Incoming Log → Process → Check Rules → Generate Alerts → Store
     │                        │
     │                        ├─ Latency > 500ms? → SLOW_API
     │                        ├─ Status >= 500?   → BROKEN_API
     │                        └─ Event = rate-hit? → RATE_LIMIT
     │
     └─ Also check for incident creation
```

## 3. Next.js Dashboard

**Technology**: Next.js 14 (App Router), TypeScript, Tailwind CSS
**Rendering**: Client-Side (CSR)

### Architecture

```
┌─────────────────────────────────────────────────┐
│              Next.js Application                │
├─────────────────────────────────────────────────┤
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │           App Router (Pages)             │  │
│  │                                          │  │
│  │  • /login        → Authentication        │  │
│  │  • /dashboard    → Main Dashboard        │  │
│  │  • /             → Redirect Logic        │  │
│  └──────────────────────────────────────────┘  │
│                    │                            │
│                    ▼                            │
│  ┌──────────────────────────────────────────┐  │
│  │          React Components                │  │
│  │                                          │  │
│  │  • StatsCard                             │  │
│  │  • LogsTable                             │  │
│  │  • AlertsList                            │  │
│  │  • IncidentsList                         │  │
│  │  • FiltersPanel                          │  │
│  └──────────────────────────────────────────┘  │
│                    │                            │
│                    ▼                            │
│  ┌──────────────────────────────────────────┐  │
│  │            API Client Layer              │  │
│  │                                          │  │
│  │  • axios interceptors                    │  │
│  │  • JWT token injection                   │  │
│  │  • Error handling                        │  │
│  └──────────────────────────────────────────┘  │
│                    │                            │
└────────────────────┼────────────────────────────┘
                     │
                     ▼ HTTP/REST
              ┌──────────────┐
              │  Collector   │
              │   Service    │
              │   :8080      │
              └──────────────┘
```

### State Management

**Client-Side State**:
- React hooks (useState, useEffect)
- localStorage for JWT token
- Component-level state

**Data Fetching**:
- On-demand loading
- Manual refresh mechanism
- No automatic polling (can be added)

### Authentication Flow

```
┌─────────┐
│ Landing │
│  Page   │
└────┬────┘
     │
     ├─ Authenticated? ──Yes──▶ Dashboard
     │
     └─ No ──▶ Login Page
                    │
                    ├─ Submit Credentials
                    │
                    ▼
             Backend Validates
                    │
                    ├─ Success → Store Token → Dashboard
                    │
                    └─ Failure → Show Error
```

## Data Flow

### End-to-End Request Flow

```
1. User API Request
   ↓
2. Microservice receives request
   ↓
3. ApiTrackingInterceptor.preHandle() → Record start time
   ↓
4. RateLimiterService.tryAcquire() → Check limit
   ↓
5. Original handler processes request
   ↓
6. ApiTrackingInterceptor.afterCompletion() → Calculate metrics
   ↓
7. LogCollectorClient.sendLog() → Async send
   ↓
8. Collector Service receives log
   ↓
9. LogCollectorService.collectLog()
   ├─ Save to Logs MongoDB
   ├─ Check alert rules
   └─ Create incidents if needed
   ↓
10. Dashboard polls/refreshes
    ↓
11. Display updated data
```

## Performance Considerations

### Bottleneck Analysis

| Component | Potential Bottleneck | Mitigation |
|-----------|---------------------|------------|
| Tracking Client | Blocking I/O | Async coroutines |
| Rate Limiter | Lock contention | Atomic operations |
| Collector Service | Concurrent writes | MongoDB write concerns |
| Logs Database | Query performance | Indexes on timestamp, serviceName |
| Dashboard | Large data loads | Pagination, filters |

### Scalability

**Horizontal Scaling**:

```
                    Load Balancer
                         │
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
    Collector-1    Collector-2    Collector-3
         │               │               │
         └───────────────┼───────────────┘
                         │
                    MongoDB Cluster
                (Replica Set / Sharded)
```

**Vertical Scaling Points**:
- Collector Service: CPU for alert processing
- MongoDB: RAM for working set
- Dashboard: Client-side rendering (no server load)

## Security Model

### Threat Model

| Threat | Mitigation |
|--------|------------|
| Unauthorized access | JWT authentication |
| Password compromise | BCrypt hashing |
| Token theft | Short expiration (24h) |
| Injection attacks | MongoDB parameterized queries |
| CORS attacks | Origin validation (production) |
| DDoS | Rate limiting at collector |

### Network Security

```
┌─────────────┐
│ Microservice│
└──────┬──────┘
       │ HTTP (internal network)
       ▼
┌─────────────┐
│  Collector  │
└──────┬──────┘
       │ JWT + HTTPS (public)
       ▼
┌─────────────┐
│  Dashboard  │
└─────────────┘
```

**Production Recommendations**:
- Use HTTPS for all communication
- VPN or private network for microservice-to-collector
- API gateway for additional security layer
- MongoDB authentication enabled
- Network segmentation

## Monitoring the Monitor

**Health Checks**:
- Collector Service: `/actuator/health` (Spring Boot)
- MongoDB: Connection pool monitoring
- Dashboard: Browser performance APIs

**Metrics to Track**:
- Log ingestion rate
- Alert generation rate
- Database query latency
- API response times
- Token validation failures

## Deployment Architecture

### Docker Deployment

```
docker-compose.yml:
  - mongodb-logs (port 27017)
  - mongodb-metadata (port 27018)
  - collector-service (port 8080)
  - dashboard (port 3000)
```

### Kubernetes Deployment (Future)

```
┌─────────────────────────────────────┐
│         Kubernetes Cluster          │
├─────────────────────────────────────┤
│                                     │
│  ┌────────────────────────────┐    │
│  │  Deployment: collector     │    │
│  │  Replicas: 3               │    │
│  │  Service: LoadBalancer     │    │
│  └────────────────────────────┘    │
│                                     │
│  ┌────────────────────────────┐    │
│  │  StatefulSet: MongoDB      │    │
│  │  Replicas: 3 (Replica Set) │    │
│  │  PersistentVolumes         │    │
│  └────────────────────────────┘    │
│                                     │
│  ┌────────────────────────────┐    │
│  │  Deployment: dashboard     │    │
│  │  Replicas: 2               │    │
│  │  Ingress: HTTPS            │    │
│  └────────────────────────────┘    │
│                                     │
└─────────────────────────────────────┘
```

## Technology Stack Summary

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Tracking Client | Kotlin, Spring Boot | API interception |
| Collector Backend | Kotlin, Spring Boot | Processing, storage |
| Database | MongoDB | Persistence |
| Frontend | Next.js, TypeScript | Visualization |
| Authentication | JWT, BCrypt | Security |
| Communication | REST, HTTP | Inter-service |
| Build Tools | Gradle, npm | Compilation |
| Deployment | Docker, Docker Compose | Containerization |

## Design Patterns Used

1. **Interceptor Pattern**: API tracking
2. **Repository Pattern**: Data access
3. **Factory Pattern**: MongoDB template creation
4. **Observer Pattern**: Alert generation
5. **Strategy Pattern**: Rate limiting algorithm
6. **Singleton Pattern**: Coroutine scope
7. **DTO Pattern**: API request/response objects

## Future Architecture Enhancements

1. **Event-Driven**: Replace REST with Kafka/RabbitMQ
2. **CQRS**: Separate read/write models
3. **Caching**: Redis for frequently accessed data
4. **Service Mesh**: Istio for advanced traffic management
5. **Tracing**: OpenTelemetry integration
6. **GraphQL**: Flexible query interface for dashboard

---

This architecture is designed for production readiness, scalability, and maintainability while keeping implementation complexity reasonable.
