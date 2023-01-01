package com.tikhon.app.database.tableobjects.entities

import org.ktorm.entity.Entity

interface Alias: Entity<Alias> {
    val id: Int
    val gitName: String
    val messengerName: String
    val gitSource: GitSource
    val messengerType: MessengerType
}