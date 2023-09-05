package com.tikhon.app.events

import com.tikhon.app.adapters.messenger.MessengerType
import com.tikhon.app.events.dto.git.GitProject
import com.tikhon.app.events.dto.git.GitSource
import com.tikhon.app.events.dto.git.GitUser
import com.tikhon.app.events.dto.git.Status

private const val SPACE = "\n     "

sealed interface IEvent

sealed interface IGitEvent : IEvent {
    val gitSource: GitSource
    fun asMessage(): String

    data class PipelineEvent(
        override val gitSource: GitSource,
        val status: Status, // TODO Остальные данные тоже переделать на DTO
        val project: GitProject,
        val branch: String,
        val users: List<GitUser>,
        val lastCommitMessage: String
    ) : IGitEvent {
        // TODO добавить ссылку на Pipeline
        // TODO добавить название если Pipeline завершился с ошибкой
        // TODO добавить ссылку на Job если Pipeline завершился с ошибкой
        override fun asMessage(): String =
            "Status:$SPACE${status.content}\n" +
            "Project:$SPACE${project.pathWithNameSpace}\n" +
            "Branch:$SPACE$branch\n" +
            "User(s):$SPACE${users.joinToString(SPACE) { "${it.name} (@${it.userName})" }}\n" +
            "Commit message:$SPACE$lastCommitMessage"
    }
}

sealed interface IMessengerEvent : IEvent {
    val messengerName: MessengerType
    val chatId: String

    // TODO По идее с точки зрения архитектуры приложения было бы правильнее делать чтобы добавление события явно требовало реализации в адаптерах
    data class ProjectSubscribeEvent(
        override val messengerName: MessengerType,
        override val chatId: String,
        val projectSource: GitSource,
        val project: GitProject
    ) : IMessengerEvent

    data class ProjectUnsubscribeEvent(
        override val messengerName: MessengerType,
        override val chatId: String,
        val projectSource: GitSource,
        val project: GitProject
    ) : IMessengerEvent

    data class AliasMakingEvent(
        override val messengerName: MessengerType,
        override val chatId: String,
        val alias: String,
        val gitLong: String,
        val gitSource: GitSource
    ) : IMessengerEvent
}

