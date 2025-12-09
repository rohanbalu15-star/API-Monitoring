package com.monitoring.client.service

import com.monitoring.client.config.MonitoringProperties
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@Service
class RateLimiterService(
    private val monitoringProperties: MonitoringProperties
) {
    private val counter = AtomicInteger(0)
    private val lastResetTime = AtomicLong(System.currentTimeMillis())
    private val windowMs = 1000L

    fun tryAcquire(): Boolean {
        val now = System.currentTimeMillis()
        val last = lastResetTime.get()

        if (now - last >= windowMs) {
            if (lastResetTime.compareAndSet(last, now)) {
                counter.set(0)
            }
        }

        val current = counter.incrementAndGet()
        return current <= monitoringProperties.rateLimit.limit
    }
}
