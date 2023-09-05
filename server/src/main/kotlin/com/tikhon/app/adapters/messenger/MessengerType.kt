package com.tikhon.app.adapters.messenger

enum class MessengerType(val type: String) {
    TG("telegram"),
    SLACK("slack"),
    DISCORD("discord"),
    WA("whats_app"),
    VK("vk");

   companion object {
       fun getOrNull(type: String): MessengerType? {
           return values().find { it.type == type }
       }
   }
}