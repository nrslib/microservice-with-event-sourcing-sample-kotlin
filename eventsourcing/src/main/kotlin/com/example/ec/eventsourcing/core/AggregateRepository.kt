package com.example.ec.eventsourcing.core

import com.example.ec.eventsourcing.core.command.Command
import com.example.ec.eventsourcing.core.entity.EntityWithIdAndVersion
import com.example.ec.eventsourcing.core.entity.EntityWithMetadata
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.eventsourcing.core.event.EventTypeAndData
import com.example.ec.eventsourcing.core.event.EventWithMetaData
import com.google.gson.Gson

class AggregateRepository<T, CT>(
    private val clazz: Class<T>,
    private val aggregateStore: AggregateStore
)
        where T : CommandProcessingAggregate<T, CT>,
              CT : Command {
    fun save(command: CT): EntityWithIdAndVersion<T> {
        val aggregate = clazz.getDeclaredConstructor().newInstance()
        val events = aggregate.processCommand(command)
        Aggregate.applyEvents(aggregate, events)

        val serializedEvents = events.map { EventTypeAndData(it.javaClass.name, Gson().toJson(it)) }
        val entityIdVersionAndEventIds = aggregateStore.save(clazz, serializedEvents)

        return EntityWithIdAndVersion(
            entityIdVersionAndEventIds,
            aggregate
        )
    }

    fun find(id: String): EntityWithMetadata<T>? {
        return aggregateStore.find(clazz, id)
    }

    fun update(id: String, cmd: CT, updateOption: UpdateOptions = UpdateOptions()): EntityWithIdAndVersion<T> {
        val loadedData = aggregateStore.find(clazz, id) ?: throw IllegalArgumentException("$id is notfound")
        val aggregate = loadedData.entity
        val events = aggregate.processCommand(cmd)
        Aggregate.applyEvents(aggregate, events)
        val idAndVersion = aggregateStore.update(
            clazz,
            loadedData.idAndVersion,
            events,
            withPossibleSnapshot(updateOption, aggregate, loadedData.snapshotVersion, loadedData.events, events)
        )

        return EntityWithIdAndVersion(
            idAndVersion,
            aggregate
        )
    }

    private fun withPossibleSnapshot(
        updateOption: UpdateOptions,
        aggregate: T,
        snapshotVersion: String?,
        oldEvents: List<EventWithMetaData>,
        newEvent: List<Event>
    ): UpdateOptions {
        val snapshot = aggregateStore.possiblySnapshot(aggregate, snapshotVersion, oldEvents, newEvent)
        return if (snapshot == null) {
            updateOption
        } else {
            updateOption.withSnapshot(snapshot)
        }
    }
}
