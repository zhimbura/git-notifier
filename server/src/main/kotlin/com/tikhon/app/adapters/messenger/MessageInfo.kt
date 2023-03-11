package com.tikhon.app.adapters.messenger

data class MessageInfo(
    val chatId: String,
    val senderLogin: String?,
    val message: String
)
