package com.tikhon.app.events


open class EventEmitter<T: Enum<T>, E: IEvent> {
    private val subscribers: MutableMap<T, MutableSet<(E) -> Unit>> = mutableMapOf()

    fun on(type: T, handle: (E) -> Unit) {
        subscribers
            .getOrPut(type) { mutableSetOf() }
            .add(handle)
    }

    fun del(type: T, handle: (E) -> Unit) {
        subscribers[type]?.remove { it === handle }
    }

    protected fun emit(eventType: T, event: E) {
        subscribers[eventType]?.forEach{ it(event) }
    }
}

