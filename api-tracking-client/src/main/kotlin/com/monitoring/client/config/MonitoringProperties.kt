package com.monitoring.client.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "monitoring")
data class MonitoringProperties(
    var collectorUrl: String = "http://localhost:8080/api/logs",
    var rateLimit: RateLimitConfig = RateLimitConfig()
)

data class RateLimitConfig(
    var service: String = "default",
    var limit: Int = 100
)
