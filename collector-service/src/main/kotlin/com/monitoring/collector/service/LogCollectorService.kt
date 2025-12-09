package com.monitoring.collector.service

import com.monitoring.collector.model.Alert
import com.monitoring.collector.model.AlertType
import com.monitoring.collector.model.ApiLog
import com.monitoring.collector.model.Incident
import com.monitoring.collector.model.IncidentStatus
import com.monitoring.collector.repository.AlertRepository
import com.monitoring.collector.repository.ApiLogRepository
import com.monitoring.collector.repository.IncidentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class LogCollectorService(
    private val apiLogRepository: ApiLogRepository,
    private val alertRepository: AlertRepository,
    private val incidentRepository: IncidentRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun collectLog(apiLog: ApiLog) {
        apiLogRepository.save(apiLog)

        scope.launch {
            processAlertsAndIncidents(apiLog)
        }
    }

    private fun processAlertsAndIncidents(apiLog: ApiLog) {
        if (apiLog.latencyMs > 500) {
            val alert = Alert(
                serviceName = apiLog.serviceName,
                endpoint = apiLog.endpoint,
                alertType = AlertType.SLOW_API,
                message = "Slow API detected: ${apiLog.endpoint} took ${apiLog.latencyMs}ms",
                timestamp = Instant.now(),
                severity = "warning",
                metadata = mapOf("latencyMs" to apiLog.latencyMs)
            )
            alertRepository.save(alert)

            createOrUpdateIncident(
                apiLog.serviceName,
                apiLog.endpoint,
                "SLOW_API",
                "Endpoint ${apiLog.endpoint} has latency over 500ms"
            )
        }

        if (apiLog.statusCode >= 500) {
            val alert = Alert(
                serviceName = apiLog.serviceName,
                endpoint = apiLog.endpoint,
                alertType = AlertType.BROKEN_API,
                message = "Broken API detected: ${apiLog.endpoint} returned ${apiLog.statusCode}",
                timestamp = Instant.now(),
                severity = "critical",
                metadata = mapOf("statusCode" to apiLog.statusCode)
            )
            alertRepository.save(alert)

            createOrUpdateIncident(
                apiLog.serviceName,
                apiLog.endpoint,
                "BROKEN_API",
                "Endpoint ${apiLog.endpoint} returned 5xx status code"
            )
        }

        if (apiLog.eventType == "rate-limit-hit") {
            val alert = Alert(
                serviceName = apiLog.serviceName,
                endpoint = apiLog.endpoint,
                alertType = AlertType.RATE_LIMIT_HIT,
                message = "Rate limit exceeded for ${apiLog.serviceName}",
                timestamp = Instant.now(),
                severity = "warning",
                metadata = mapOf("endpoint" to apiLog.endpoint)
            )
            alertRepository.save(alert)
        }
    }

    private fun createOrUpdateIncident(
        serviceName: String,
        endpoint: String,
        type: String,
        description: String
    ) {
        val existingIncidents = incidentRepository.findByStatus(IncidentStatus.OPEN)
        val exists = existingIncidents.any {
            it.serviceName == serviceName && it.endpoint == endpoint && it.incidentType == type
        }

        if (!exists) {
            val incident = Incident(
                serviceName = serviceName,
                endpoint = endpoint,
                incidentType = type,
                description = description,
                status = IncidentStatus.OPEN
            )
            incidentRepository.save(incident)
        }
    }

    fun getAllLogs(): List<ApiLog> {
        return apiLogRepository.findAll()
    }

    fun getFilteredLogs(
        serviceName: String?,
        endpoint: String?,
        startDate: Instant?,
        endDate: Instant?,
        statusCode: Int?,
        slowApi: Boolean,
        brokenApi: Boolean,
        rateLimitHit: Boolean
    ): List<ApiLog> {
        return apiLogRepository.findByFilters(
            serviceName, endpoint, startDate, endDate, statusCode, slowApi, brokenApi, rateLimitHit
        )
    }

    fun getAllAlerts(): List<Alert> {
        return alertRepository.findAll()
    }

    fun getRecentAlerts(limit: Int = 50): List<Alert> {
        return alertRepository.findRecent(limit)
    }

    fun getStats(): Map<String, Any> {
        return mapOf(
            "slowApiCount" to apiLogRepository.countSlowApis(),
            "brokenApiCount" to apiLogRepository.countBrokenApis(),
            "rateLimitViolations" to apiLogRepository.countRateLimitHits()
        )
    }
}
