package com.monitoring.client.interceptor

import com.monitoring.client.config.MonitoringProperties
import com.monitoring.client.model.ApiLogEvent
import com.monitoring.client.service.LogCollectorClient
import com.monitoring.client.service.RateLimiterService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Instant

@Component
class ApiTrackingInterceptor(
    private val monitoringProperties: MonitoringProperties,
    private val logCollectorClient: LogCollectorClient,
    private val rateLimiterService: RateLimiterService
) : HandlerInterceptor {

    companion object {
        private const val START_TIME_ATTR = "startTime"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis())
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        try {
            val startTime = request.getAttribute(START_TIME_ATTR) as? Long ?: return
            val latency = System.currentTimeMillis() - startTime

            val requestSize = request.contentLength.toLong().coerceAtLeast(0)
            val responseSize = if (response is ContentCachingResponseWrapper) {
                response.contentSize.toLong()
            } else {
                0L
            }

            val logEvent = ApiLogEvent(
                serviceName = monitoringProperties.rateLimit.service,
                endpoint = request.requestURI,
                method = request.method,
                requestSize = requestSize,
                responseSize = responseSize,
                statusCode = response.status,
                timestamp = Instant.now(),
                latencyMs = latency,
                eventType = "api-call"
            )

            logCollectorClient.sendLog(logEvent)

            if (!rateLimiterService.tryAcquire()) {
                val rateLimitEvent = logEvent.copy(
                    eventType = "rate-limit-hit"
                )
                logCollectorClient.sendLog(rateLimitEvent)
            }
        } catch (e: Exception) {
            // Silently fail to not impact application
        }
    }
}
