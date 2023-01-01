package com.tikhon.app.database.tableobjects.entities

import org.ktorm.entity.Entity

interface MessengerType: Entity<MessengerType> {
    val id: Int
    val name: String
}