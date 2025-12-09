package com.monitoring.collector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "alerts")
data class Alert(
    @Id
    val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val alertType: AlertType,
    val message: String,
    val timestamp: Instant,
    val severity: String = "warning",
    val metadata: Map<String, Any> = emptyMap()
)

enum class AlertType {
    SLOW_API,
    BROKEN_API,
    RATE_LIMIT_HIT
}
