package com.tikhon.app.adapters.messenger

import com.tikhon.app.events.EventEmitter
import com.tikhon.app.events.IMessengerEvent
import com.tikhon.app.events.MessengerEventType
import com.tikhon.app.events.dto.git.GitProject
import com.tikhon.app.events.dto.git.GitSource
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

    abstract fun sendMessage(chatId: String, message: String)

    protected abstract fun beforeStart()

    protected fun addProject(chatId: String, message: String) {
        val projectSource = try {
            GitSource.fromURL(message)
        } catch (e: Exception) {
            sendMessage(chatId, "Невозможно разобрать ссылку на проект, пример правильной ссылки http://gitlab.south.rt.ru/epc/ompo-sdk-kotlin")
            return
        }
        val projectInfo = try {
            GitProject.fromURL(message)
        } catch (e: Exception) {
            sendMessage(chatId, "Невозможно разобрать ссылку на проект, пример правильной ссылки http://gitlab.south.rt.ru/epc/ompo-sdk-kotlin")
            return
        }
        val event = IMessengerEvent.ProjectSubscribeEvent(
            messengerName,
            chatId,
            projectSource,
            projectInfo
        )
        this.emit(MessengerEventType.ADD_PROJECT, event)
    }

    protected fun todo(chatId: String, message: String) {
        sendMessage(chatId, message)
    }

    companion object {
        private val adapters: MutableMap<String, MessengerAdapter> = mutableMapOf()

        private fun register(type: String, adapter: MessengerAdapter) {
            require(!adapters.containsKey(type)) { "Адаптеры должны быть созданы только в одном экземпляре" }
            adapters[type] = adapter
        }

        fun getAdapter(type: String) : MessengerAdapter {
            return adapters[type] ?: throw IllegalStateException("Попытка получить ре реализованный адаптер")
        }
    }
}