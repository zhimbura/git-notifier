package com.tikhon.app.events

import com.tikhon.app.events.dto.git.GitProject
import com.tikhon.app.events.dto.git.GitSource
import com.tikhon.app.events.dto.git.GitUser
import com.tikhon.app.events.dto.git.Status

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
        override fun asMessage(): String = """
            Status: ${status.content}
            Project: ${project.pathWithNameSpace}
            Branch: $branch
            User(s): ${users.joinToString(",") { "${it.name} (@${it.userName})" }}
            Commit message: $lastCommitMessage
        """.trimIndent()
    }
}

sealed interface IMessengerEvent : IEvent {
    val messengerName: String

    // TODO По идее с точки зрения архитектуры приложения было бы правильнее делать чтобы добавление события явно требовало реализации в адаптерах
    data class ProjectSubscribeEvent(
        override val messengerName: String,
        val chatId: String,
        val projectSource: GitSource
    ) : IMessengerEvent

    data class ProjectUnsubscribeEvent(
        override val messengerName: String,
        val chatId: String,
        val projectSource: GitSource
    ) : IMessengerEvent

}

