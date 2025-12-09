package com.monitoring.collector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "api_logs")
data class ApiLog(
    @Id
    val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val method: String,
    val requestSize: Long,
    val responseSize: Long,
    val statusCode: Int,
    val timestamp: Instant,
    val latencyMs: Long,
    val eventType: String = "api-call"
)
