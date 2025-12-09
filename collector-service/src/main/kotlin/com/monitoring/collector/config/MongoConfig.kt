package com.monitoring.collector.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

@Configuration
class MongoConfig {

    @Bean(name = ["logsMongoClient"])
    @Primary
    fun logsMongoClient(logsMongoProperties: LogsMongoProperties): MongoClient {
        return MongoClients.create(logsMongoProperties.uri)
    }

    @Bean(name = ["metadataMongoClient"])
    fun metadataMongoClient(metadataMongoProperties: MetadataMongoProperties): MongoClient {
        return MongoClients.create(metadataMongoProperties.uri)
    }

    @Bean(name = ["logsMongoDatabaseFactory"])
    @Primary
    fun logsMongoDatabaseFactory(
        @Qualifier("logsMongoClient") mongoClient: MongoClient,
        logsMongoProperties: LogsMongoProperties
    ): MongoDatabaseFactory {
        return SimpleMongoClientDatabaseFactory(mongoClient, logsMongoProperties.database)
    }

    @Bean(name = ["metadataMongoDatabaseFactory"])
    fun metadataMongoDatabaseFactory(
        @Qualifier("metadataMongoClient") mongoClient: MongoClient,
        metadataMongoProperties: MetadataMongoProperties
    ): MongoDatabaseFactory {
        return SimpleMongoClientDatabaseFactory(mongoClient, metadataMongoProperties.database)
    }

    @Bean(name = ["logsMongoTemplate"])
    @Primary
    fun logsMongoTemplate(
        @Qualifier("logsMongoDatabaseFactory") mongoDatabaseFactory: MongoDatabaseFactory
    ): MongoTemplate {
        return MongoTemplate(mongoDatabaseFactory)
    }

    @Bean(name = ["metadataMongoTemplate"])
    fun metadataMongoTemplate(
        @Qualifier("metadataMongoDatabaseFactory") mongoDatabaseFactory: MongoDatabaseFactory
    ): MongoTemplate {
        return MongoTemplate(mongoDatabaseFactory)
    }

    @Bean(name = ["logsTransactionManager"])
    @Primary
    fun logsTransactionManager(
        @Qualifier("logsMongoDatabaseFactory") mongoDatabaseFactory: MongoDatabaseFactory
    ): MongoTransactionManager {
        return MongoTransactionManager(mongoDatabaseFactory)
    }

    @Bean(name = ["metadataTransactionManager"])
    fun metadataTransactionManager(
        @Qualifier("metadataMongoDatabaseFactory") mongoDatabaseFactory: MongoDatabaseFactory
    ): MongoTransactionManager {
        return MongoTransactionManager(mongoDatabaseFactory)
    }
}

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.logs")
class LogsMongoProperties {
    var uri: String = "mongodb://localhost:27017"
    var database: String = "monitoring_logs"
}

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.metadata")
class MetadataMongoProperties {
    var uri: String = "mongodb://localhost:27018"
    var database: String = "monitoring_metadata"
}
