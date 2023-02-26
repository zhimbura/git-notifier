package com.tikhon.app.adapters.messenger

import com.tikhon.app.events.EventEmitter
import com.tikhon.app.events.IMessengerEvent
import com.tikhon.app.events.MessengerEventType
import java.lang.IllegalStateException

abstract class MessengerAdapter(val messengerName: String) : EventEmitter<MessengerEventType, IMessengerEvent>() {
    private var state: MessengerAdapterState = MessengerAdapterState.CREATED

    init {
        register(messengerName, this)
    }

    fun start() {
        try {
            this.beforeStart()
            this.state = MessengerAdapterState.STARTED
        } catch (ex: Exception) {
            ex.printStackTrace() // TODO logger
            this.state = MessengerAdapterState.FAILED
        }
    }

    public abstract fun sendMessage(chatId: String, message: String)

    protected abstract fun beforeStart()

    companion object {
        private val adapters: MutableMap<String, MessengerAdapter> = mutableMapOf()

        private fun register(type: String, adapter: MessengerAdapter) {
            require(!adapters.containsKey(type)) { "Адаптеры должны быть созданы только в одном экземпляре" }
            adapters[type] = adapter
        }

        public fun getAdapter(type: String) : MessengerAdapter {
            return adapters[type] ?: throw IllegalStateException("Попытка получить ре реализованный адаптер")
        }
    }
}