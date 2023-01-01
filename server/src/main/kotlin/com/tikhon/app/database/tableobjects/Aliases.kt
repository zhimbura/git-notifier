package com.tikhon.app.database.tableobjects

import com.tikhon.app.database.tableobjects.entities.Alias
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Aliases : Table<Alias>("t_aliases") {
    val id = int("id").primaryKey().bindTo { it.id }
    val gitName = varchar("git_name").bindTo { it.gitName }
    val messengerName = varchar("messenger_name").bindTo { it.messengerName }
    val gitSourceId = int("git_source_id").references(GitSources) { it.gitSource }
    val messengerTypeId = int("messenger_type_id").references(MessengerTypes) { it.messengerType }
}