package com.tikhon.app

import com.tikhon.app.adapters.messenger.TelegramAdapter
import com.tikhon.app.database.DatabaseConnect
import com.tikhon.app.events.IEvent
import com.tikhon.app.events.IGitEvent
import com.tikhon.app.events.IMessengerEvent

class ApplicationCore {
    val connect = DatabaseConnect()
    // Получаем уведомление о добавлении проекта, записываем в какой чат какой проект был добавлен

    init {
        TelegramAdapter.start()
        connect.checkConnect()
    }

   fun receiveEvent(event: IEvent) {
       when (event) {
           is IGitEvent -> {}
           is IMessengerEvent -> {}
       }
   }



}