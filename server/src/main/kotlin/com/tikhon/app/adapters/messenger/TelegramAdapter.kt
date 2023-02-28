package com.tikhon.app.adapters.messenger

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode

val DEV_MSG = """
    В данный момент бот находится в разработке
    Если вы знаете одно из: Kotlin, PostgreSQL, Docker, ci/cd, то можете подключиться к разработке проекта по адресу https://github.com/Tihon-Ustinov/git-notifier
    Или можете написать мне в личные сообщения @your_rubicon с вопросом чем можно помочь 
""".trimIndent()

fun createBotAPI(vararg commands: Pair<String, (Long, String) -> Unit>) = bot {
    token = System.getenv("TG_API_TOKEN")
    dispatch {
        text {
            if (commands.none { (key, _) -> message.text?.startsWith("/$key") == true }) {
                bot.sendMessage(ChatId.fromId(message.chat.id), text = DEV_MSG)
            }
        }
        for ((cmdName, handler) in commands) {
            this.command(cmdName) {
                val textMessage = this.message.text
                    ?: return@command // TODO вместо return
                val firstSpace = textMessage
                    .indexOf(' ')
                    .takeIf { it > -1 }
                    ?: return@command
                val textWithoutCommand = textMessage.substring(firstSpace + 1).trim()
                handler(message.chat.id, textWithoutCommand)
            }
        }
    }

}

class TelegramAdapter : MessengerAdapter("telegram") {
    private val bot = createBotAPI(
        "addproject" to this::addProject,
        "help" to this::todo,
        "aliasme" to this::todo,
        "removeproject" to this::todo,
        "subscribe" to this::todo,
        "unsubscribe" to this::todo,
        "showprojects" to this::todo,
        "showsubscribe" to this::todo
    )

    override fun beforeStart() {
        bot.startPolling()
    }

    override fun sendMessage(chatId: String, message: String) {
        val chat = chatId.toChatId()
        bot.sendMessage(chat, message, parseMode = ParseMode.MARKDOWN)
    }

    private fun addProject(chatId: Long, message: String) {
       // TODO("Реализовать добавление проекта") перенести абстрактные команды в родительский класс
        todo(chatId, message)
    }

    private fun todo(chatId: Long, message: String) {
        bot.sendMessage(chatId.toChatId(), message)
    }
}


private fun Long.toChatId() = ChatId.fromId(this)

private fun String.toChatId() = this.toLongOrNull()?.toChatId() ?: ChatId.fromChannelUsername(this)