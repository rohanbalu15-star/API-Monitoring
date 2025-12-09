package com.monitoring.collector.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "incidents")
data class Incident(
    @Id
    val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val incidentType: String,
    val description: String,
    val status: IncidentStatus = IncidentStatus.OPEN,
    val createdAt: Instant = Instant.now(),
    val resolvedAt: Instant? = null,
    val resolvedBy: String? = null,
    @Version
    val version: Long? = null
)

enum class IncidentStatus {
    OPEN,
    RESOLVED
}
