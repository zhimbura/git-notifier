package com.tikhon.app.adapters.messenger

import com.tikhon.app.adapters.messenger.telegram.TelegramAdapter
import com.tikhon.app.adapters.messenger.telegram.TelegramHTMLMessageBuilder

abstract class MessageBuilder {

    private val message: StringBuilder = StringBuilder()
    open val indent = "     "

    inline fun buildMessage(builderAction: MessageBuilder.() -> Unit): String {
        return this.apply(builderAction).build()
    }

    fun appendLine(value: String) = apply { message.appendLine(value) }
    fun append(value: String) = apply { message.append(value) }

    open fun indent(value: String) = "$indent$value"
    open fun escape(value: String) = value
    open fun bold(value: String) = value
    open fun link(title: String, url: String) = "$title ($url)"

    fun build(): String = message.toString()
}

class DefaultMessageBuilder : MessageBuilder()

fun getMessageBuilder(adapter: MessengerAdapter): MessageBuilder {
    return when(adapter) {
        is TelegramAdapter -> TelegramHTMLMessageBuilder()
        else -> DefaultMessageBuilder()
    }
}