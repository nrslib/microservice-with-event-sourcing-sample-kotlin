package com.example.ec.eventsourcing.core

import com.example.ec.eventsourcing.core.command.Command
import com.example.ec.eventsourcing.core.event.Event

open class CommandProcessingAggregate<T : CommandProcessingAggregate<T, CT>, CT> : Aggregate<T>
        where CT : Command {
    override fun applyEvent(event: Event) {
        javaClass.getMethod("apply", event.javaClass).invoke(this, event)
    }

    fun processCommand(command: CT): List<Event> {
        return javaClass.getMethod("process", command.javaClass).invoke(this, command) as List<Event>
    }
}