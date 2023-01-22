package com.tikhon.app.events

import com.tikhon.app.events.dto.git.GitProject
import com.tikhon.app.events.dto.git.GitSource
import com.tikhon.app.events.dto.git.GitUser
import com.tikhon.app.events.dto.git.Status

sealed interface IEvent

interface IGitEvent : IEvent {
    val gitSource: GitSource
    data class PipelineEvent(
        override val gitSource: GitSource,
        val status: Status, // TODO Остальные данные тоже переделать на DTO
        val project: GitProject,
        val branch: String,
        val users: List<GitUser>
    ) : IGitEvent
}

interface IMessengerEvent: IEvent {
    val messengerName: String
}

