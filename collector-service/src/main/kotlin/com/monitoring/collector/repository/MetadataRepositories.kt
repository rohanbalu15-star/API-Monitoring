package com.monitoring.collector.repository

import com.monitoring.collector.model.Incident
import com.monitoring.collector.model.IncidentStatus
import com.monitoring.collector.model.RateLimitConfig
import com.monitoring.collector.model.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class UserRepository(
    @Qualifier("metadataMongoTemplate") private val mongoTemplate: MongoTemplate
) {
    fun save(user: User): User {
        return mongoTemplate.save(user, "users")
    }

    fun findByUsername(username: String): User? {
        val query = Query(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne(query, User::class.java, "users")
    }

    fun existsByUsername(username: String): Boolean {
        val query = Query(Criteria.where("username").`is`(username))
        return mongoTemplate.exists(query, User::class.java, "users")
    }
}

@Repository
class IncidentRepository(
    @Qualifier("metadataMongoTemplate") private val mongoTemplate: MongoTemplate
) {
    fun save(incident: Incident): Incident {
        return mongoTemplate.save(incident, "incidents")
    }

    fun findAll(): List<Incident> {
        return mongoTemplate.findAll(Incident::class.java, "incidents")
    }

    fun findById(id: String): Incident? {
        return mongoTemplate.findById(id, Incident::class.java, "incidents")
    }

    fun findByStatus(status: IncidentStatus): List<Incident> {
        val query = Query(Criteria.where("status").`is`(status))
        return mongoTemplate.find(query, Incident::class.java, "incidents")
    }

    fun resolveIncident(id: String, resolvedBy: String): Boolean {
        val query = Query(Criteria.where("_id").`is`(id).and("status").`is`(IncidentStatus.OPEN))
        val update = Update()
            .set("status", IncidentStatus.RESOLVED)
            .set("resolvedAt", Instant.now())
            .set("resolvedBy", resolvedBy)
            .inc("version", 1)

        val result = mongoTemplate.updateFirst(query, update, Incident::class.java, "incidents")
        return result.modifiedCount > 0
    }
}

@Repository
class RateLimitConfigRepository(
    @Qualifier("metadataMongoTemplate") private val mongoTemplate: MongoTemplate
) {
    fun save(config: RateLimitConfig): RateLimitConfig {
        return mongoTemplate.save(config, "rate_limit_configs")
    }

    fun findByServiceName(serviceName: String): RateLimitConfig? {
        val query = Query(Criteria.where("serviceName").`is`(serviceName))
        return mongoTemplate.findOne(query, RateLimitConfig::class.java, "rate_limit_configs")
    }

    fun findAll(): List<RateLimitConfig> {
        return mongoTemplate.findAll(RateLimitConfig::class.java, "rate_limit_configs")
    }
}
