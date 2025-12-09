package com.monitoring.client.service

import com.monitoring.client.config.MonitoringProperties
import com.monitoring.client.model.ApiLogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class LogCollectorClient(
    private val monitoringProperties: MonitoringProperties,
    private val restTemplate: RestTemplate = RestTemplate()
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun sendLog(event: ApiLogEvent) {
        scope.launch {
            try {
                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_JSON

                val request = HttpEntity(event, headers)
                restTemplate.postForEntity(
                    monitoringProperties.collectorUrl,
                    request,
                    String::class.java
                )
            } catch (e: Exception) {
                // Silently fail to not impact application
            }
        }
    }
}
