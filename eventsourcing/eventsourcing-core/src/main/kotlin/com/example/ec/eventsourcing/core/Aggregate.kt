package com.example.ec.eventsourcing.core

import com.example.ec.eventsourcing.core.event.Event

interface Aggregate<T>
        where T : Aggregate<T> {
    fun applyEvent(event: Event)

    companion object {
        fun <T> applyEvents(aAggregate: T, events: List<Event>): T where T : Aggregate<T> {
            var aggregate = aAggregate
            for (event in events) {
                aggregate.applyEvent(event)
            }

            return aggregate
        }

        fun <T> recreate(clazz: Class<T>, events: List<Event>): T where T : Aggregate<T> {
            val instance = clazz.getDeclaredConstructor().newInstance()
            return applyEvents(instance, events)
        }
    }
}