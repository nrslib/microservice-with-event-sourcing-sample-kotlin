# micorservice-with-event-sourcing-sample-kotlin

Event Sourcing Exercises.  
Maybe it should work.

## Project

### eventsourcing

Event Sourcing by Jpa or Cosmos DB.

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
