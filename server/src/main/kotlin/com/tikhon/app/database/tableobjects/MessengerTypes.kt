package com.tikhon.app.database.tableobjects

import com.tikhon.app.database.tableobjects.entities.MessengerType
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object MessengerTypes : Table<MessengerType>("t_messenger_type") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
}