package com.example.ec.eventsourcing.cosmos

import com.azure.cosmos.CosmosClient
import com.azure.cosmos.CosmosContainer
import com.azure.cosmos.TransactionalBatch
import com.azure.cosmos.TransactionalBatchItemRequestOptions
import com.azure.cosmos.models.CosmosQueryRequestOptions
import com.azure.cosmos.models.PartitionKey
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
import com.example.ec.eventsourcing.cosmos.document.DocumentType
import com.example.ec.eventsourcing.cosmos.document.EventSourcingDocument
import com.example.ec.eventsourcing.cosmos.entity.EntityDataModel
import com.example.ec.eventsourcing.cosmos.event.EventDataModel
import com.example.ec.eventsourcing.cosmos.snapshot.SnapshotDataModel
import com.google.gson.Gson
import java.util.*

@Suppress("DEPRECATION")
class CosmosAggregateStore(
    private val cosmosClient: CosmosClient,
    private val snapshotManager: SnapshotManager,
    private val databaseName: String,
    private val documentName: String,

) : AggregateStore {
    init {
        val db = cosmosClient.getDatabase(databaseName)
        val result = db.createContainerIfNotExists(documentName, "/entityId")
    }

    override fun <T : Aggregate<T>> save(
        clazz: Class<T>,
        events: List<EventTypeAndData>,
        options: AggregateCrudSaveOptions?
    ): EntityIdAndVersion {

        val container = getContainer()

        val entityId = UUID.randomUUID().toString()
        val version = events.count().toLong()
        val batch = TransactionalBatch.createTransactionalBatch(PartitionKey(entityId))

        val entityDataModel = EntityDataModel(
            entityId,
            clazz.name,
            version
        )
        val entityDocument = entityDataModel.toDocument()
        batch.createItemOperation(entityDocument)

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
            .forEach { batch.createItemOperation(it) }

        val response = container.executeTransactionalBatch(batch)
        if (!response.isSuccessStatusCode) {
            throw UnsupportedOperationException()
        }

        return EntityIdAndVersion(
            entityId,
            version
        )
    }

    override fun <T : Aggregate<T>> find(clazz: Class<T>, id: String): EntityWithMetadata<T>? {
        val container = getContainer()

        val readSnapshotDocumentResponse = container.queryItems(
            "SELECT * FROM x WHERE x.type = \"${DocumentType.Snapshot}\" AND x.entityId = \"${id}\" ORDER BY x.version DESC",
            CosmosQueryRequestOptions().setPartitionKey(PartitionKey(id)).setMaxBufferedItemCount(1),
            EventSourcingDocument::class.java
        )
        val snapshotDataModel = if (readSnapshotDocumentResponse.any()) {
            val snapshotDocument = readSnapshotDocumentResponse.first()

            SnapshotDataModel.from(snapshotDocument)
        } else {
            null
        }

        val readEntityDocumentResponse = container.readItem(id, PartitionKey(id), EventSourcingDocument::class.java)
        val entityDocument = readEntityDocumentResponse.item
        val entity = EntityDataModel.from(entityDocument)

        val getEventsQuery = if (snapshotDataModel != null) {
            "SELECT * FROM x WHERE x.type = \"${DocumentType.Event}\" AND x.version > ${entity.version} AND x.entityId = \"${entity.id}\" ORDER BY x.version"
        } else {
            "SELECT * FROM x WHERE x.type = \"${DocumentType.Event}\" AND x.entityId = \"${entity.id}\" ORDER BY x.version"
        }

        val eventDocuments = container.queryItems(
            getEventsQuery,
            CosmosQueryRequestOptions().setPartitionKey(PartitionKey(entity.id)),
            EventSourcingDocument::class.java
        )

        val events = eventDocuments.map(EventDataModel.Companion::from).map {
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
        val batch = TransactionalBatch.createTransactionalBatch(PartitionKey(entityId))

        val container = getContainer()

        val entityDocumentReadResponse =
            container.readItem(entityId, PartitionKey(entityId), EventSourcingDocument::class.java)
        val entityDocument = entityDocumentReadResponse.item

        if (currentVersion != entityDocument.version) {
            throw OptimisticConcurrencyException(entityId, currentVersion, entityDocument.version)
        }

        val eventDatamodels = events.mapIndexed { index, it ->
            EventDataModel(
                UUID.randomUUID().toString(),
                it.javaClass.name,
                clazz.name,
                entityId,
                currentVersion + index + 1,
                Gson().toJson(it)
            )
        }

        eventDatamodels
            .map(EventDataModel::toDocument)
            .forEach {
                batch.createItemOperation(it)
            }

        val latestVersion = eventDatamodels.last().version
        val entity = EntityDataModel.from(entityDocument)
        entity.version = latestVersion
        val replaceEntityDocument = entity.toDocument()
        batch.replaceItemOperation(
            entityId,
            replaceEntityDocument,
            TransactionalBatchItemRequestOptions().setIfMatchETag(entityDocument.etag)
        )

        if (options?.snapshot != null) {
            val snapshot = options.snapshot
            val snapshotDataModel = SnapshotDataModel(
                UUID.randomUUID().toString(),
                clazz.name,
                entityIdAndVersion.id,
                latestVersion,
                snapshot.javaClass.name,
                Gson().toJson(snapshot)
            )
            val snapshotDocument = snapshotDataModel.toDocument()
            batch.createItemOperation(snapshotDocument)
        }

        val result = container.executeTransactionalBatch(batch)
        if (!result.isSuccessStatusCode) {
            throw UnsupportedOperationException()
        }

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

    private fun getContainer(): CosmosContainer {
        val db = cosmosClient.getDatabase(databaseName)
        val eventSourcingContainer = db.getContainer(documentName)

        return eventSourcingContainer
    }
}