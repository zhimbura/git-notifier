package com.tikhon.app.events

import com.tikhon.app.events.dto.git.GitSource

sealed interface IEvent

interface IGitEvent : IEvent {
    val gitSource: GitSource
    data class PipelineEvent(
        override val gitSource: GitSource,
        val status: String, // TODO Остальные данные тоже переделать на DTO
        val project: String,
        val branch: String,
        val users: List<String>
    ) : IGitEvent
}

interface IMessengerEvent: IEvent {
    val messengerName: String
}

