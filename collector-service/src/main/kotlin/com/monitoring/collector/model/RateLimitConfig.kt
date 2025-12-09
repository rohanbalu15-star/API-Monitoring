package com.monitoring.collector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "rate_limit_configs")
data class RateLimitConfig(
    @Id
    val id: String? = null,
    val serviceName: String,
    val limit: Int,
    val enabled: Boolean = true
)
