package com.tikhon.app

import com.tikhon.app.adapters.messenger.MessengerAdapter
import com.tikhon.app.adapters.messenger.MessengerAdapter.Companion.getAdapter
import com.tikhon.app.adapters.messenger.TelegramAdapter
import com.tikhon.app.database.DatabaseConnect
import com.tikhon.app.events.IGitEvent
import com.tikhon.app.events.IMessengerEvent
import com.tikhon.app.events.MessengerEventType

class ApplicationCore {
    val connect = DatabaseConnect()
    // Получаем уведомление о добавлении проекта, записываем в какой чат какой проект был добавлен
    val telegramAdapter = TelegramAdapter()
    init {
        connect.checkConnect()
        telegramAdapter.start()
        telegramAdapter.on(MessengerEventType.ADD_PROJECT) { receiveMessengerEvent(it, telegramAdapter) }
    }

    fun receiveMessengerEvent(event: IMessengerEvent, messenger: MessengerAdapter) {
        when (event) {
            is IMessengerEvent.ProjectSubscribeEvent -> {
                messenger.sendMessage(event.chatId, "Проект распознан ${event.project}")
                // TODO добавить в базу гит, проект и подписку
            }

            is IMessengerEvent.ProjectUnsubscribeEvent -> {

            }
        }
    }

    fun receiveGitEvent(event: IGitEvent) {
        when (event) {
            is IGitEvent.PipelineEvent -> {
                val messengers = connect.getMessagesByRepository(
                    event.gitSource.name,
                    event.project.pathWithNameSpace,
                ) ?: return

                for ((type, chats) in messengers.entries) {
                    val messengerAdapter = getAdapter(type)
                    for (chatId in chats) {
                        messengerAdapter.sendMessage(chatId, event.asMessage())
                    }
                }
            }
        }
    }
}