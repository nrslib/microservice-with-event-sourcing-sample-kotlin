package com.example.ec.eventsourcing.dynamo.document

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

enum class DocumentType {
    Entity,
    Event,
    Snapshot
}

@DynamoDbBean
class EventSourcingDocument {
    lateinit var id: String

    @get:DynamoDbAttribute("document_type")
    lateinit var documentType: DocumentType

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("entity_id")
    lateinit var entityId: String
    lateinit var body: String

    /**
     * documentType
     *  Entity: "Entity"
     *  Event: "Event#{version}"
     *  Snapshot: "Snapshot#{version}"
     */
    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("document_type_to_version")
    lateinit var documentTypeToVersion: String

    var version: Long = -1
}