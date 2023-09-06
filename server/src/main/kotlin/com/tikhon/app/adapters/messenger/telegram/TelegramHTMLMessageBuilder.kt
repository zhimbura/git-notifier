package com.tikhon.app.adapters.messenger.telegram

import com.tikhon.app.adapters.messenger.MessageBuilder

class TelegramHTMLMessageBuilder : MessageBuilder() {

    override fun escape(value: String) = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

    override fun bold(value: String) = "<b>$value</b>"

    override fun link(title: String, url: String) = "<a href=\"$url\">$title</a>"
}