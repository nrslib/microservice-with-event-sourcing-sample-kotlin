# micorservice-with-event-sourcing-sample-kotlin

Event Sourcing Exercises.  
Maybe it should work.

## Project

### eventsourcing

Event Sourcing by Jpa or DynamoDB or Cosmos DB.

#### Dynamo DB Sample

Entry Point
```Application.kt
import com.example.ec.eventsourcing.dynamo.DynamoEventSourcingConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(DynamoEventSourcingConfig::class)
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
```

application.yml
```application.yml
event-sourcing:
  dynamo:
    access-key-id: ${DYNAMO_ACCESS_KEY_ID}
    secret-access-key: ${DYNAMO_SECRET_ACCESS_KEY}
```

build.gradle.kts
```build.gradle.kts
dependencies {
    ...
    
    // Infrastructure:event sourcing
    implementation("com.example.ec:eventsourcing-core:1.0.0")
    implementation("com.example.ec:eventsourcing-dynamo:1.0.0")
}
```

#### Cosmos DB Sample

Entry Point
```Application.kt
import com.example.ec.eventsourcing.cosmos.CosmosEventSourcingConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(CosmosEventSourcingConfig::class)
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
```

application.yml
```application.yml
azure:
  cosmos:
    uri: ${YOUR_AZURE_COSMOS_URI}
    key: ${YOUR_AZURE_COSMOS_KEY}
    database: ${YOUR_AZURE_COSMOS_DATABASE}
event-sourcing:
  cosmos:
    database-name: ${YOUR_COSMOS_DATABASE_NAME}
```

build.gradle.kts
```build.gradle.kts
dependencies {
    ...
    
    implementation("com.example.ec:eventsourcing-core:1.0.0")
    implementation("com.example.ec:eventsourcing-cosmos:1.0.0")
}
```

### order-app\order

Sample (OrderService)