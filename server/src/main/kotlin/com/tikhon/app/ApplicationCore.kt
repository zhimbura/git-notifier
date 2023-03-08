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
                val gitSourceId = connect.addGitSource(
                    event.projectSource.name,
                    event.projectSource.url
                )
                val repositoryId = connect.addRepository(
                    event.project.pathWithNameSpace,
                    event.project.name,
                    gitSourceId
                )
                val messengerTypeId = connect.addMessengerType(event.messengerName)
                val success = connect.addSubscribe(event.chatId, messengerTypeId, repositoryId)
                if (success) {
                    messenger.sendMessage(event.chatId, "Проект успешно добавлен. Теперь в этот чат будут отправляться обрабатываемые события этого проекта")
                } else {
                    messenger.sendMessage(event.chatId, "Данный проект уже добавлен")
                }
            }

            is IMessengerEvent.ProjectUnsubscribeEvent -> {
                // TODO Добавить удаление
            }

            is IMessengerEvent.AliasMakingEvent -> {
                // TODO Добавить создание линковки логина гита и мессенджера
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