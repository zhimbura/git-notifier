package com.tikhon.app.database.tableobjects.entities

import org.ktorm.entity.Entity

interface Alias: Entity<Alias> {
    val id: Int
    val gitLogin: String
    val messengerLogin: String
    val gitSource: GitSource
    val messengerType: MessengerType
}