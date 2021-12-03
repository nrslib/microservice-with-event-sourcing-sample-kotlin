package com.example.ec.applicationsupportstack.microservicesupport.saga

class SagaManager {
    private val sagas = mutableListOf<Saga>()

    fun register(saga: Saga) {
        sagas.add(saga)
    }

    fun start(command: Any) {
        sagas.forEach { saga ->
            val handlers = saga.javaClass.methods
                    .filter { it.name == "handle" }
                    .filter {
                        it.parameters.any { param ->
                            param.type.name == command.javaClass.typeName
                        }
                    }
            handlers.forEach { m ->
                m.invoke(saga, command)
            }
        }
    }
}
