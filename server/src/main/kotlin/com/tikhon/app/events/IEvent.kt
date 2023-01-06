package com.tikhon.app.events

sealed interface IEvent

interface IGitEvent : IEvent {
    val gitSource: String
}

interface IMessengerEvent: IEvent {
    val messengerName: String
}

