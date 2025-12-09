package com.monitoring.collector.controller

import com.monitoring.collector.repository.ApiLogRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController(
    @Qualifier("logsMongoTemplate") private val mongoTemplate: MongoTemplate,
    private val apiLogRepository: ApiLogRepository
) {

    @GetMapping("/avg-latency")
    fun getAvgLatencyPerEndpoint(): ResponseEntity<*> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.group("endpoint")
                .avg("latencyMs").`as`("avgLatency")
                .count().`as`("count"),
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "avgLatency"),
            Aggregation.limit(20)
        )

        val results = mongoTemplate.aggregate(
            aggregation,
            "api_logs",
            Map::class.java
        ).mappedResults

        return ResponseEntity.ok(results)
    }

    @GetMapping("/top-slow-endpoints")
    fun getTopSlowEndpoints(@RequestParam(defaultValue = "5") limit: Int): ResponseEntity<*> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("latencyMs").gt(500)),
            Aggregation.group("endpoint", "serviceName")
                .avg("latencyMs").`as`("avgLatency")
                .max("latencyMs").`as`("maxLatency")
                .count().`as`("count"),
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "avgLatency"),
            Aggregation.limit(limit.toLong())
        )

        val results = mongoTemplate.aggregate(
            aggregation,
            "api_logs",
            Map::class.java
        ).mappedResults

        return ResponseEntity.ok(results)
    }

    @GetMapping("/error-rate")
    fun getErrorRate(): ResponseEntity<*> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.group()
                .count().`as`("total")
                .sum(
                    org.springframework.data.mongodb.core.aggregation.ConditionalOperators
                        .`when`(Criteria.where("statusCode").gte(500))
                        .then(1)
                        .otherwise(0)
                ).`as`("errors")
        )

        val results = mongoTemplate.aggregate(
            aggregation,
            "api_logs",
            Map::class.java
        ).mappedResults

        if (results.isNotEmpty()) {
            val result = results[0]
            val total = (result["total"] as? Number)?.toLong() ?: 0L
            val errors = (result["errors"] as? Number)?.toLong() ?: 0L
            val errorRate = if (total > 0) (errors.toDouble() / total.toDouble()) * 100 else 0.0

            return ResponseEntity.ok(
                mapOf(
                    "total" to total,
                    "errors" to errors,
                    "errorRate" to errorRate
                )
            )
        }

        return ResponseEntity.ok(
            mapOf(
                "total" to 0,
                "errors" to 0,
                "errorRate" to 0.0
            )
        )
    }

    @GetMapping("/timeline")
    fun getTimeline(@RequestParam(defaultValue = "24") hours: Int): ResponseEntity<*> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.group(
                org.springframework.data.mongodb.core.aggregation.DateOperators.DateToString
                    .dateOf("timestamp")
                    .toString("%Y-%m-%d %H:00:00")
            )
                .count().`as`("requests")
                .avg("latencyMs").`as`("avgLatency")
                .sum(
                    org.springframework.data.mongodb.core.aggregation.ConditionalOperators
                        .`when`(Criteria.where("statusCode").gte(500))
                        .then(1)
                        .otherwise(0)
                ).`as`("errors"),
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "_id"),
            Aggregation.limit(hours.toLong())
        )

        val results = mongoTemplate.aggregate(
            aggregation,
            "api_logs",
            Map::class.java
        ).mappedResults

        return ResponseEntity.ok(results)
    }
}
