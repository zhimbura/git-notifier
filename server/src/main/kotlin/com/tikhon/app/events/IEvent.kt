package com.tikhon.app.events

import com.tikhon.app.adapters.messenger.MessageBuilder
import com.tikhon.app.events.dto.git.*

sealed interface IEvent

sealed interface IGitEvent : IEvent {
    val gitSource: GitSource
    fun asMessage(builder: MessageBuilder): String

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
        override fun asMessage(builder: MessageBuilder) = builder.buildMessage {
            appendLine(bold("Status:"))
            appendLine(indent(escape(status.content)))

            appendLine(bold("Project:"))
            appendLine(indent(escape(project.pathWithNameSpace)))

            appendLine(bold("Branch:"))
            appendLine(indent(escape(branch)))

            appendLine(bold("User(s)"))
            appendLine(indent(escape(users.joinToString("\n$indent") { "${it.name} (@${it.userName})" })))

            appendLine(bold("Commit message:"))
            appendLine(indent(escape(lastCommitMessage)))
        }
    }

    data class MergeRequestEvent(
        override val gitSource: GitSource,
        val project: GitProject,
        val mergeRequest: GitMergeRequest
    ) : IGitEvent {

        override fun asMessage(builder: MessageBuilder) = builder.buildMessage {
            appendLine("\uD83D\uDCA1${bold("New merge request")}\uD83D\uDCA1\n")

            with(mergeRequest) {
                appendLine(bold("Project:"))
                appendLine(indent(escape("${project.pathWithNameSpace} ($sourceBranch -> $targetBranch)")))

                appendLine(bold("Title:"))
                appendLine(indent(link(escape(title), mergeRequest.url)))

                if (description.isNotBlank()) {
                    appendLine(bold("Description:"))
                    appendLine(indent(escape(description)))
                }

                if (reviewers.isNotEmpty()) {
                    val reviewersList = reviewers.joinToString("\n$indent") { "${it.name} (@${it.userName})" }
                    appendLine(bold("Reviewers:"))
                    appendLine(indent(escape(reviewersList)))
                }

                appendLine(bold("\nAuthor:"))
                appendLine(indent(escape("${author.name} (@${author.userName})")))
            }

        }

    }
}

sealed interface IMessengerEvent : IEvent {
    val messengerName: String
    val chatId: String

    // TODO По идее с точки зрения архитектуры приложения было бы правильнее делать чтобы добавление события явно требовало реализации в адаптерах
    data class ProjectSubscribeEvent(
        override val messengerName: String,
        override val chatId: String,
        val projectSource: GitSource,
        val project: GitProject
    ) : IMessengerEvent

    data class ProjectUnsubscribeEvent(
        override val messengerName: String,
        override val chatId: String,
        val projectSource: GitSource,
        val project: GitProject
    ) : IMessengerEvent

    data class AliasMakingEvent(
        override val messengerName: String,
        override val chatId: String,
        val alias: String,
        val gitLong: String,
        val gitSource: GitSource
    ) : IMessengerEvent
}

