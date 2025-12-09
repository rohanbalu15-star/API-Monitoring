package com.monitoring.collector.repository

import com.monitoring.collector.config.LogsMongoTemplate
import com.monitoring.collector.model.Alert
import com.monitoring.collector.model.AlertType
import com.monitoring.collector.model.ApiLog
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogsMongoTemplate

@Repository
@LogsMongoTemplate
class ApiLogRepository(
    @Qualifier("logsMongoTemplate") private val mongoTemplate: MongoTemplate
) {
    fun save(apiLog: ApiLog): ApiLog {
        return mongoTemplate.save(apiLog)
    }

    fun findAll(): List<ApiLog> {
        return mongoTemplate.findAll(ApiLog::class.java)
    }

    fun findByFilters(
        serviceName: String? = null,
        endpoint: String? = null,
        startDate: Instant? = null,
        endDate: Instant? = null,
        statusCode: Int? = null,
        slowApi: Boolean = false,
        brokenApi: Boolean = false,
        rateLimitHit: Boolean = false
    ): List<ApiLog> {
        val query = Query()

        serviceName?.let { query.addCriteria(Criteria.where("serviceName").`is`(it)) }
        endpoint?.let { query.addCriteria(Criteria.where("endpoint").`is`(it)) }
        startDate?.let { query.addCriteria(Criteria.where("timestamp").gte(it)) }
        endDate?.let { query.addCriteria(Criteria.where("timestamp").lte(it)) }
        statusCode?.let { query.addCriteria(Criteria.where("statusCode").`is`(it)) }

        if (slowApi) query.addCriteria(Criteria.where("latencyMs").gt(500))
        if (brokenApi) query.addCriteria(Criteria.where("statusCode").gte(500))
        if (rateLimitHit) query.addCriteria(Criteria.where("eventType").`is`("rate-limit-hit"))

        return mongoTemplate.find(query, ApiLog::class.java)
    }

    fun countSlowApis(): Long {
        val query = Query(Criteria.where("latencyMs").gt(500))
        return mongoTemplate.count(query, ApiLog::class.java)
    }

    fun countBrokenApis(): Long {
        val query = Query(Criteria.where("statusCode").gte(500))
        return mongoTemplate.count(query, ApiLog::class.java)
    }

    fun countRateLimitHits(): Long {
        val query = Query(Criteria.where("eventType").`is`("rate-limit-hit"))
        return mongoTemplate.count(query, ApiLog::class.java)
    }
}

@Repository
@LogsMongoTemplate
class AlertRepository(
    @Qualifier("logsMongoTemplate") private val mongoTemplate: MongoTemplate
) {
    fun save(alert: Alert): Alert {
        return mongoTemplate.save(alert)
    }

    fun findAll(): List<Alert> {
        return mongoTemplate.findAll(Alert::class.java)
    }

    fun findByType(type: AlertType): List<Alert> {
        val query = Query(Criteria.where("alertType").`is`(type))
        return mongoTemplate.find(query, Alert::class.java)
    }

    fun findRecent(limit: Int = 50): List<Alert> {
        val query = Query().limit(limit)
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"))
        return mongoTemplate.find(query, Alert::class.java)
    }
}
