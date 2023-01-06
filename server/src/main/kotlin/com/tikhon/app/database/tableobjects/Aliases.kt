package com.tikhon.app.database.tableobjects

import com.tikhon.app.database.tableobjects.entities.Alias
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Aliases : Table<Alias>("t_aliases") {
    val id = int("id").primaryKey().bindTo { it.id }
    val gitLogin = varchar("git_login").bindTo { it.gitLogin }
    val messengerLogin = varchar("messenger_login").bindTo { it.messengerLogin }
    val gitSourceId = int("git_source_id").references(GitSources) { it.gitSource }
    val messengerTypeId = int("messenger_type_id").references(MessengerTypes) { it.messengerType }
}