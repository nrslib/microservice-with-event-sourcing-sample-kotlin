package com.example.ec.eventsourcing.jpa

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
import com.example.ec.eventsourcing.jpa.entity.EntityDataModel
import com.example.ec.eventsourcing.jpa.entity.EntityJpaRepository
import com.example.ec.eventsourcing.jpa.entity.EntityKey
import com.example.ec.eventsourcing.jpa.event.EventDataModel
import com.example.ec.eventsourcing.jpa.event.EventDataModelJpaRepository
import com.example.ec.eventsourcing.jpa.snapshot.SnapshotDataModel
import com.example.ec.eventsourcing.jpa.snapshot.SnapshotJpaRepository
import com.google.gson.Gson
import java.util.*

class JpaAggregateStore(
    private val eventRepository: EventDataModelJpaRepository,
    private val entityRepository: EntityJpaRepository,
    private val snapshotRepository: SnapshotJpaRepository,
    private val snapshotManager: SnapshotManager
) : AggregateStore {

    override fun <T> save(
        clazz: Class<T>,
        events: List<EventTypeAndData>,
        options: AggregateCrudSaveOptions?
    ): EntityIdAndVersion
            where T : Aggregate<T> {
        val entity = createOrLoadEntity(clazz.name, options)

        val eventDataModels = events.map {
            EventDataModel(
                it.eventType,
                clazz.name,
                entity.id,
                it.eventData
            )
        }
        val savedEvents = eventRepository.saveAll(eventDataModels)
        val latestVersion = savedEvents.last().id
        entity.version = latestVersion.toString()
        entityRepository.save(entity)

        return EntityIdAndVersion(
            entity.id,
            latestVersion
        )
    }

    private fun createOrLoadEntity(aggregateType: String, options: AggregateCrudSaveOptions?): EntityDataModel {
        return if (options != null) {
            val entity = entityRepository.findById(EntityKey(aggregateType, options.entityId)).orElseThrow()

            entity
        } else {
            val entity = EntityDataModel()
            entity.id = UUID.randomUUID().toString()
            entity.version = "0"
            entity.type = aggregateType

            entity
        }
    }

    override fun <T> find(clazz: Class<T>, id: String): EntityWithMetadata<T>? where T : Aggregate<T> {
        val snapshotDataModel =
            snapshotRepository.findFirstByEntityIdAndEntityTypeOrderByEntityVersionDesc(id, clazz.name)

        val eventDataModels = if (snapshotDataModel != null) {
            eventRepository.findByEntityTypeAndEntityIdAndIdGreaterThanOrderById(
                clazz.name,
                id,
                snapshotDataModel.entityVersion.toLong()
            )
        } else {
            eventRepository.findByEntity(id, clazz.name)
        }

        val events = eventDataModels.map {
            val eventClazz = Class.forName(it.eventType)
            val event = Gson().fromJson(it.payload, eventClazz) as Event
            EventWithMetaData(event, it.id)
        }

        if (snapshotDataModel == null && events.isEmpty()) {
            return null
        }

        val version = if (events.isEmpty()) {
            snapshotDataModel!!.entityVersion.toLong()
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
            snapshotDataModel?.entityVersion
        )
    }

    override fun <T> update(
        clazz: Class<T>,
        idAndVersion: EntityIdAndVersion,
        events: List<Event>,
        options: UpdateOptions?
    ): EntityIdAndVersion where T : Aggregate<T> {
        val entity = entityRepository.findById(EntityKey(clazz.name, idAndVersion.id)).orElseThrow()

        if (entity.version.toLong() != idAndVersion.version) {
            throw OptimisticConcurrencyException(idAndVersion.id, idAndVersion.version, entity.version.toLong())
        }

        val gson = Gson()
        val eventDataModels = events.map {
            val eventClazz = it.javaClass
            EventDataModel(
                eventClazz.name,
                clazz.name,
                idAndVersion.id,
                gson.toJson(it)
            )
        }
        val savedEventDataModels = eventRepository.saveAll(eventDataModels)
        val newVersion = savedEventDataModels.last().id

        entity.version = newVersion.toString()
        val result = entityRepository.save(entity)

        if (options != null) {
            if (options.snapshot != null) {
                val snapshot = options.snapshot
                snapshotRepository.save(
                    SnapshotDataModel(
                        clazz.name,
                        idAndVersion.id,
                        newVersion.toString(),
                        snapshot.javaClass.name,
                        gson.toJson(snapshot)
                    )
                )
            }
        }

        return EntityIdAndVersion(result.id, result.version.toLong())
    }

    override fun possiblySnapshot(
        aggregate: Aggregate<*>,
        snapshotVersion: String?,
        oldEvents: List<EventWithMetaData>,
        newEvents: List<Event>,
    ): Snapshot? {
        return snapshotManager.possiblySnapshot(aggregate, oldEvents, newEvents, snapshotVersion)
    }

    override fun <T> recreateAggregate(clasz: Class<T>, snapshot: Snapshot): T where T : Aggregate<T> {
        return snapshotManager.recreateFromSnapshot(clasz, snapshot)
    }
}