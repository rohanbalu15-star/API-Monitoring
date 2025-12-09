package com.monitoring.collector.controller

import com.monitoring.collector.model.ApiLog
import com.monitoring.collector.service.LogCollectorService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api")
class LogController(
    private val logCollectorService: LogCollectorService
) {

    @PostMapping("/logs")
    fun collectLog(@RequestBody apiLog: ApiLog): ResponseEntity<*> {
        logCollectorService.collectLog(apiLog)
        return ResponseEntity.ok(mapOf("message" to "Log collected successfully"))
    }

    @GetMapping("/logs")
    fun getLogs(
        @RequestParam(required = false) serviceName: String?,
        @RequestParam(required = false) endpoint: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: Instant?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Instant?,
        @RequestParam(required = false) statusCode: Int?,
        @RequestParam(required = false, defaultValue = "false") slowApi: Boolean,
        @RequestParam(required = false, defaultValue = "false") brokenApi: Boolean,
        @RequestParam(required = false, defaultValue = "false") rateLimitHit: Boolean
    ): ResponseEntity<List<ApiLog>> {
        val logs = logCollectorService.getFilteredLogs(
            serviceName, endpoint, startDate, endDate, statusCode, slowApi, brokenApi, rateLimitHit
        )
        return ResponseEntity.ok(logs)
    }

    @GetMapping("/alerts")
    fun getAlerts(@RequestParam(required = false, defaultValue = "50") limit: Int): ResponseEntity<*> {
        val alerts = logCollectorService.getRecentAlerts(limit)
        return ResponseEntity.ok(alerts)
    }

    @GetMapping("/stats")
    fun getStats(): ResponseEntity<Map<String, Any>> {
        val stats = logCollectorService.getStats()
        return ResponseEntity.ok(stats)
    }
}
