package com.monitoring.client.model

import java.time.Instant

data class ApiLogEvent(
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
