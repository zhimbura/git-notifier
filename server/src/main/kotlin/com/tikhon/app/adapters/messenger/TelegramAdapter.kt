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

fun createBotAPI(vararg commands: Pair<String, (MessageInfo) -> Unit>) = bot {
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
                handler(
                    MessageInfo(
                        chatId = message.chat.id.toString(),
                        senderLogin = message.from?.username,
                        message = textWithoutCommand
                    )
                )
            }
        }
    }

}

class TelegramAdapter : MessengerAdapter("telegram") {
    private val bot = createBotAPI(
        "addproject" to this::addProject,
        "help" to this::todo,
        "aliasme" to this::addAlias,
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
        bot.sendMessage(chat, message, ParseMode.MARKDOWN)
    }

    override fun notifyAll(chatId: String, message: String) {
        val chat = chatId.toChatId()
        bot.sendMessage(chat, message, ParseMode.MARKDOWN)
            .fold(
                ifSuccess = { msg -> bot.pinChatMessage(chat, msg.messageId) },
                ifError = {}
        )
    }
}


private fun Long.toChatId() = ChatId.fromId(this)

private fun String.toChatId() = this.toLongOrNull()?.toChatId() ?: ChatId.fromChannelUsername(this)