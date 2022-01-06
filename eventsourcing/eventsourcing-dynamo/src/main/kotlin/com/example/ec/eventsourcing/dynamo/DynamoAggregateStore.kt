package com.example.ec.eventsourcing.dynamo

import com.example.ec.eventsourcing.core.Aggregate
import com.example.ec.eventsourcing.core.AggregateCrudSaveOptions
import com.example.ec.eventsourcing.core.AggregateStore
import com.example.ec.eventsourcing.core.UpdateOptions
import com.example.ec.eventsourcing.core.entity.EntityIdAndVersion
import com.example.ec.eventsourcing.core.entity.EntityWithMetadata
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.eventsourcing.core.event.EventTypeAndData
import com.example.ec.eventsourcing.core.event.EventWithMetaData
import com.example.ec.eventsourcing.core.exception.OptimisticConcurrencyException
import com.example.ec.eventsourcing.core.snapshot.Snapshot
import com.example.ec.eventsourcing.core.snapshot.SnapshotManager
import com.example.ec.eventsourcing.dynamo.document.DocumentType
import com.example.ec.eventsourcing.dynamo.document.EventSourcingDocument
import com.example.ec.eventsourcing.dynamo.entity.EntityDataModel
import com.example.ec.eventsourcing.dynamo.event.EventDataModel
import com.example.ec.eventsourcing.dynamo.snapshot.SnapshotDataModel
import com.google.gson.Gson
import software.amazon.awssdk.enhanced.dynamodb.*
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.TransactUpdateItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException
import java.util.*

class DynamoAggregateStore(
    private val client: DynamoDbEnhancedClient,
    private val snapshotManager: SnapshotManager,
    private val tableName: String
) : AggregateStore {
    init {
        val table = getTable()
        try {
            table.describeTable()
        } catch (e: ResourceNotFoundException) {
            table.createTable()
        }
    }

    override fun <T : Aggregate<T>> save(
        clazz: Class<T>,
        events: List<EventTypeAndData>,
        options: AggregateCrudSaveOptions?
    ): EntityIdAndVersion {
        val entityId = UUID.randomUUID().toString()
        val version = events.count().toLong()

        val entityDataModel = EntityDataModel(
            entityId,
            clazz.name,
            version
        )
        val entityDocument = entityDataModel.toDocument()

        val table = getTable()
        val writeItemsBuilder = TransactWriteItemsEnhancedRequest.builder()
            .addPutItem(table, entityDocument)
        events.mapIndexed { index, it ->
            EventDataModel(
                UUID.randomUUID().toString(),
                it.eventType,
                clazz.name,
                entityId,
                index.toLong() + 1,
                it.eventData
            )
        }.map(EventDataModel::toDocument)
            .forEach { writeItemsBuilder.addPutItem(table, it) }

        client.transactWriteItems(writeItemsBuilder.build())

        return EntityIdAndVersion(entityId, version)
    }

    override fun <T : Aggregate<T>> find(clazz: Class<T>, id: String): EntityWithMetadata<T>? {
        val table = getTable()

        val conditionEqualsEntityId = makeQueryConditionEqualToEntityId(id)

        val snapshotQuery = table.query { r ->
            r.queryConditional(conditionEqualsEntityId)
                .filterExpression(documentTypeExpression(DocumentType.Snapshot).build())
                .scanIndexForward(false)
                .limit(1)
        }

        val snapshotDataModel = snapshotQuery
            .items()
            .map(SnapshotDataModel.Companion::from)
            .firstOrNull()

        val entity = getEntityDataModel(id, table)

        val eventsQueryConditional = QueryConditional.sortBeginsWith {
            it.partitionValue(id)
                .sortValue(DocumentType.Event.toString() + "#")
        }

        val eventsQuery = table.query { r ->
            var conditional = r.queryConditional(eventsQueryConditional)
            if (snapshotDataModel != null) {
                conditional = conditional.filterExpression(
                    Expression.builder().expression("version > :version")
                        .putExpressionValue(":version", AttributeValues.numberValue(entity.version)).build()
                )
            }
            conditional.scanIndexForward(true)
        }
        val eventsDataModel = eventsQuery.items().map(EventDataModel.Companion::from)
        val events = eventsDataModel.map {
            val eventClazz = Class.forName(it.eventType)
            val event = Gson().fromJson(it.payload, eventClazz) as Event
            EventWithMetaData(event, it.version)
        }

        if (snapshotDataModel == null && events.isEmpty()) {
            return null
        }

        val version = if (events.isEmpty()) {
            entity.version
        } else {
            events.last().version
        }

        val idAndVersion = EntityIdAndVersion(id, version)
        val recreatedAggregate = if (snapshotDataModel != null) {
            val snapshotClazz = Class.forName(snapshotDataModel.snapshotType)
            val snapshot = Gson().fromJson(snapshotDataModel.snapshotData, snapshotClazz) as Snapshot
            val aggregate = snapshotManager.recreateFromSnapshot(clazz, snapshot)
            Aggregate.applyEvents(aggregate, events.map { it.event })
        } else {
            Aggregate.recreate(clazz, events.map { it.event })
        }

        return EntityWithMetadata(
            idAndVersion,
            events,
            recreatedAggregate,
            snapshotDataModel?.entityVersion.toString()
        )
    }

    override fun <T : Aggregate<T>> update(
        clazz: Class<T>,
        entityIdAndVersion: EntityIdAndVersion,
        events: List<Event>,
        options: UpdateOptions?
    ): EntityIdAndVersion {
        val entityId = entityIdAndVersion.id
        val currentVersion = entityIdAndVersion.version

        val table = getTable()
        val entity = getEntityDataModel(entityId, table)

        if (currentVersion != entity.version) {
            throw OptimisticConcurrencyException(entityId, currentVersion, entity.version)
        }

        val eventDataModels = events.mapIndexed { index, it ->
            EventDataModel(
                UUID.randomUUID().toString(),
                it.javaClass.name,
                clazz.name,
                entityId,
                currentVersion + index + 1,
                Gson().toJson(it)
            )
        }

        val transactWriteItemBuilder = TransactWriteItemsEnhancedRequest.builder()
        eventDataModels
            .map(EventDataModel::toDocument)
            .forEach {
                transactWriteItemBuilder.addPutItem(table, it)
            }

        val latestVersion = eventDataModels.last().version
        entity.version = latestVersion
        val replaceEntityDocument = entity.toDocument()
        val updateEntityRequest = TransactUpdateItemEnhancedRequest.builder(EventSourcingDocument::class.java)
            .conditionExpression(
                Expression.builder()
                    .expression("version = :version")
                    .putExpressionValue(
                        ":version",
                        AttributeValues.numberValue(currentVersion)
                    )
                    .build()
            )
            .item(replaceEntityDocument)
            .build()

        transactWriteItemBuilder.addUpdateItem(table, updateEntityRequest)


        if (options?.snapshot != null) {
            val snapshot = options.snapshot
            val snapshotDataModel = SnapshotDataModel(
                UUID.randomUUID().toString(),
                clazz.name,
                entityIdAndVersion.id,
                latestVersion,
                snapshot!!.javaClass.name,
                Gson().toJson(snapshot)
            )
            val snapshotDocument = snapshotDataModel.toDocument()
            transactWriteItemBuilder.addPutItem(table, snapshotDocument)
        }

        client.transactWriteItems(transactWriteItemBuilder.build())

        return EntityIdAndVersion(entity.id, latestVersion)
    }

    override fun possiblySnapshot(
        aggregate: Aggregate<*>,
        snapshotVersion: String?,
        oldEvents: List<EventWithMetaData>,
        newEvents: List<Event>
    ): Snapshot? {
        return snapshotManager.possiblySnapshot(aggregate, oldEvents, newEvents, snapshotVersion)
    }

    override fun <T : Aggregate<T>> recreateAggregate(clasz: Class<T>, snapshot: Snapshot): T {
        return snapshotManager.recreateFromSnapshot(clasz, snapshot)
    }

    private fun getTable(): DynamoDbTable<EventSourcingDocument> {
        return client.table(tableName, TableSchema.fromBean(EventSourcingDocument::class.java))
    }

    private fun getEntityDataModel(id: String, table: DynamoDbTable<EventSourcingDocument>): EntityDataModel {
        val entityQuery = table.query { r ->
            r.queryConditional(makeQueryConditionEqualToEntityId(id))
                .filterExpression(documentTypeExpression(DocumentType.Entity).build())
        }
        return entityQuery.items().map(EntityDataModel.Companion::from).first()
    }

    private fun makeQueryConditionEqualToEntityId(id: String): QueryConditional {
        return QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(id)
                .build()
        )
    }

    private fun documentTypeExpression(type: DocumentType): Expression.Builder {
        return Expression.builder()
            .expression("document_type = :document_type")
            .putExpressionValue(
                ":document_type",
                AttributeValues.stringValue(type.toString())
            )
    }
}