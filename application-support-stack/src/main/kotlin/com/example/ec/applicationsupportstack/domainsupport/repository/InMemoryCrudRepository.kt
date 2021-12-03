//package com.example.ec.applicationsupportstack.domainsupport.repository
//
//import com.example.ec.applicationsupportstack.domainsupport.aggregate.AggregateRoot
//import org.springframework.context.ApplicationEventPublisher
//
//abstract class InMemoryCrudRepository<K, V>(
//        private val applicationEventPublisher: ApplicationEventPublisher
//) {
//    val keyToValue = HashMap<K, V>()
//
//    fun save(value: V) {
//        val key = getKey(value)
//        val cloned = deepClone(value)
//        keyToValue[key] = cloned
//
//        onSaved(cloned)
//
//        if (value is AggregateRoot<*>) {
//            value.domainEvents().forEach(applicationEventPublisher::publishEvent)
//            value.clearDomainEvents()
//        }
//    }
//
//    fun find(key: K): V? {
//        val target = keyToValue[key] ?: return null
//
//        val cloned = deepClone(target)
//
//        return cloned
//    }
//
//    fun delete(value: V) {
//        val key = getKey(value)
//        keyToValue.remove(key)
//
//        onDeleted(value)
//    }
//
//    abstract fun getKey(value: V): K
//    abstract fun deepClone(value: V): V
//    open fun onSaved(value: V) {
//    }
//
//    open fun onDeleted(value: V) {
//    }
//}