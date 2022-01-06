package com.example.ec.eventsourcing.jpa.entity

import org.springframework.data.repository.CrudRepository

interface EntityJpaRepository : CrudRepository<EntityDataModel, EntityKey>