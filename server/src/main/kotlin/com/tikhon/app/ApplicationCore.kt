package com.tikhon.app

import com.tikhon.app.adapters.messenger.MessengerAdapter.Companion.getAdapter
import com.tikhon.app.adapters.messenger.TelegramAdapter
import com.tikhon.app.database.DatabaseConnect
import com.tikhon.app.events.IEvent
import com.tikhon.app.events.IGitEvent
import com.tikhon.app.events.IMessengerEvent

class ApplicationCore {
    val connect = DatabaseConnect()
    // Получаем уведомление о добавлении проекта, записываем в какой чат какой проект был добавлен
    val telegramAdapter = TelegramAdapter()
    init {
        connect.checkConnect()
        telegramAdapter.start()
    }

    fun receiveEvent(event: IEvent) {
        when (event) {
            is IGitEvent -> receiveGitEvent(event)
            is IMessengerEvent -> {}
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
            else -> {
                // TODO Добавить остальные события и разбить по функциям
            }
        }
    }
}