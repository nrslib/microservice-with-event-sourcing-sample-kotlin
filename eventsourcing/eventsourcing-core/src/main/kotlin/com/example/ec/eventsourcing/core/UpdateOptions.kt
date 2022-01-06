package com.example.ec.eventsourcing.core

import com.example.ec.eventsourcing.core.snapshot.Snapshot

class UpdateOptions(
    val snapshot: Snapshot? = null
) {
    fun withSnapshot(snapshot: Snapshot): UpdateOptions {
        return UpdateOptions(snapshot)
    }
}