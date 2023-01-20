package com.tikhon.app.adapters.messenger

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId

val DEV_MSG = """
    В данный момент бот находится в разработке
    Если вы знаете одно из: Kotlin, PostgreSQL, Docker, ci/cd, то можете подключиться к разработке проекта по адресу https://github.com/Tihon-Ustinov/git-notifier
    Или можете написать мне в личные сообщения @your_rubicon с вопросом чем можно помочь 
""".trimIndent()

private val bot = bot {
    token = System.getenv("TG_API_TOKEN")
    dispatch {
        text {
            bot.sendMessage(ChatId.fromId(message.chat.id), text = DEV_MSG)
        }
    }
}

object TelegramAdapter : MessengerAdapter("Telegram") {
    override fun beforeStart() {
        bot.startPolling()
    }

    fun sendMessage(chatId: Long, text: String) {
        bot.sendMessage(ChatId.fromId(chatId), text)
    }
}