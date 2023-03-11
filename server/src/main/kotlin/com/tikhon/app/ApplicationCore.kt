package com.tikhon.app

import com.tikhon.app.adapters.messenger.MessengerAdapter
import com.tikhon.app.adapters.messenger.MessengerAdapter.Companion.getAdapter
import com.tikhon.app.adapters.messenger.TelegramAdapter
import com.tikhon.app.database.DatabaseConnect
import com.tikhon.app.database.ResultType.*
import com.tikhon.app.events.IGitEvent
import com.tikhon.app.events.IMessengerEvent
import com.tikhon.app.events.MessengerEventType
import com.tikhon.app.events.dto.git.GitUser

class ApplicationCore {
    val connect = DatabaseConnect()

    // Получаем уведомление о добавлении проекта, записываем в какой чат какой проект был добавлен
    val telegramAdapter = TelegramAdapter()

    init {
        connect.checkConnect()
        telegramAdapter.on(MessengerEventType.NEW_MESSAGE) { receiveMessengerEvent(it, telegramAdapter) }
        telegramAdapter.start()
    }

    private fun receiveMessengerEvent(event: IMessengerEvent, messenger: MessengerAdapter) {
        when (event) {
            is IMessengerEvent.ProjectSubscribeEvent -> addProject(event, messenger)

            is IMessengerEvent.ProjectUnsubscribeEvent -> {
                // TODO Добавить удаление
            }

            is IMessengerEvent.AliasMakingEvent -> addAlias(event, messenger)
        }
    }

    private fun addAlias(event: IMessengerEvent.AliasMakingEvent, messenger: MessengerAdapter) {
        val gitSourceId = connect.addGitSource(
            event.gitSource.name,
            event.gitSource.url
        )
        val messengerTypeId = connect.addMessengerType(event.messengerName)
        val result = connect.addAlias(
            gitLogin = event.gitLong,
            messengerLogin = event.alias,
            gitSourceId = gitSourceId,
            messengerTypeId = messengerTypeId
        )
        when (result) {
            SUCCESS -> messenger.sendMessage(
                event.chatId,
                "Пара-логин создана, теперь username git будет меняться на username в этом мессенджере"
            )

            NO_CHANGE -> messenger.sendMessage(event.chatId, "Пара-логин уже создана")
            ERROR -> messenger.sendMessage(event.chatId, "Ошибка создания пары-логина, попробуйте позже")
        }
    }

    private fun addProject(event: IMessengerEvent.ProjectSubscribeEvent, messenger: MessengerAdapter) {
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
        when (connect.addSubscribe(event.chatId, messengerTypeId, repositoryId)) {
            SUCCESS -> messenger.sendMessage(
                event.chatId,
                "Проект успешно добавлен. Теперь в этот чат будут отправляться обрабатываемые события этого проекта"
            )

            NO_CHANGE -> messenger.sendMessage(event.chatId, "Данный проект уже добавлен")
            ERROR -> messenger.sendMessage(event.chatId, "Ошибка добавления проекта, попробуйте позже")
        }
    }

    fun receiveGitEvent(event: IGitEvent) {
        when (event) {
            is IGitEvent.PipelineEvent -> {
                val messengers = connect.getMessagesByRepository(
                    event.gitSource.url,
                    event.project.pathWithNameSpace,
                ) ?: return


                for ((type, chats) in messengers.entries) {
                    val messengerAdapter = getAdapter(type)
                    val eventWithAliases = event.copy(
                        users = event.users.map {
                            GitUser(
                                it.name,
                                connect.getAlias(
                                    gitLogin = it.userName,
                                    gitSource = event.gitSource.url,
                                    messengerType = type
                                ) ?: it.userName
                            )
                        }
                    )
                    for (chatId in chats) {
                        messengerAdapter.sendMessage(chatId, eventWithAliases.asMessage())
                    }
                }
            }
        }
    }
}