package com.example.ec.eventsourcing.dynamo

import com.example.ec.eventsourcing.core.snapshot.SnapshotManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class DynamoEventSourcingConfig(
    @Value("\${event-sourcing.dynamo.table-name:event_sourcing}") private val tableName: String,
    @Value("\${event-sourcing.dynamo.access-key-id}") private val accessKeyId: String,
    @Value("\${event-sourcing.dynamo.secret-access-key}") private val secretAccessKey: String,
    @Value("\${event-sourcing.dynamo.region:ap-northeast-1}") private val regionText: String
) {
    @Bean
    fun aggregateStore(dynamoDbEnhancedClient: DynamoDbEnhancedClient, snapshotManager: SnapshotManager): DynamoAggregateStore {
        return DynamoAggregateStore(dynamoDbEnhancedClient, snapshotManager, tableName)
    }

    @Bean
    fun dynamoDbClient(): DynamoDbClient {
        val credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey)
        val provider = StaticCredentialsProvider.create(credentials)

        return DynamoDbClient.builder()
            .region(Region.of(regionText))
            .credentialsProvider(provider)
            .build()
    }

    @Bean
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()
    }

    @Bean
    fun snapshotManager(): SnapshotManager {
        return SnapshotManager()
    }
}