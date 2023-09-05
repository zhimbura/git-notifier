package com.tikhon.app.adapters.messenger

import com.tikhon.app.events.EventEmitter
import com.tikhon.app.events.IMessengerEvent
import com.tikhon.app.events.MessengerEventType
import com.tikhon.app.events.dto.git.GitProject
import com.tikhon.app.events.dto.git.GitSource
import java.lang.IllegalStateException

abstract class MessengerAdapter(val messengerName: MessengerType) : EventEmitter<MessengerEventType, IMessengerEvent>() {
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

    protected fun addProject(messageInfo: MessageInfo) {
        val projectSource = try {
            GitSource.fromURL(messageInfo.message)
        } catch (e: Exception) {
            sendMessage(messageInfo.chatId, "Невозможно разобрать ссылку на проект, пример правильной ссылки http://gitlab.south.rt.ru/epc/ompo-sdk-kotlin")
            return
        }
        val projectInfo = try {
            GitProject.fromURL(messageInfo.message)
        } catch (e: Exception) {
            sendMessage(messageInfo.chatId, "Невозможно разобрать ссылку на проект, пример правильной ссылки http://gitlab.south.rt.ru/epc/ompo-sdk-kotlin")
            return
        }
        val event = IMessengerEvent.ProjectSubscribeEvent(
            messengerName,
            messageInfo.chatId,
            projectSource,
            projectInfo
        )
        this.emit(MessengerEventType.NEW_MESSAGE, event)
    }

    protected fun addAlias(messageInfo: MessageInfo) {
        if (messageInfo.senderLogin == null) {
            sendMessage(messageInfo.chatId, "Не возможно создать связь пары-логин так как у вас не установлен userName")
            return
        }
        val gitSource = try {
            GitSource.fromURL(messageInfo.message)
        } catch (err: Exception) {
            sendMessage(messageInfo.chatId, "Не удалось прочитать ссылку")
            return
        }
        val regex = Regex("[^/]+\$")
        val match = regex.find(messageInfo.message)
        if (match == null || match.groupValues.size != 1 || match.groupValues.last().isEmpty()) {
            sendMessage(messageInfo.chatId, "Не удалось получить git username")
            return
        }
        val event = IMessengerEvent.AliasMakingEvent(
            messengerName = messengerName,
            chatId = messageInfo.chatId,
            alias = messageInfo.senderLogin,
            gitLong = match.groupValues.last(),
            gitSource = gitSource
        )
        this.emit(MessengerEventType.NEW_MESSAGE, event)
    }
    protected fun todo(messageInfo: MessageInfo) {
        sendMessage(messageInfo.chatId, "Пока что реализованы функции подписки и создания связи пар-логинов")
    }

    companion object {
        private val adapters: MutableMap<MessengerType, MessengerAdapter> = mutableMapOf()

        private fun register(type: MessengerType, adapter: MessengerAdapter) {
            require(!adapters.containsKey(type)) { "Адаптеры должны быть созданы только в одном экземпляре" }
            adapters[type] = adapter
        }

        fun getAdapter(type: MessengerType) : MessengerAdapter {
            return adapters[type] ?: throw IllegalStateException("Попытка получить ре реализованный адаптер")
        }

        fun getAdapterOrNull(type: MessengerType) : MessengerAdapter? {
            return adapters[type]
        }
    }
}