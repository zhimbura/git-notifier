package com.tikhon.app.database.tableobjects

import com.tikhon.app.database.tableobjects.entities.RepositorySubscriber
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object RepositorySubscribes : Table<RepositorySubscriber>("t_repository_subscribes") {
    val id = int("id").primaryKey().bindTo { it.id }
    val chatId = varchar("chat_id").bindTo { it.chatId }
    val messengerTypeId = int("messenger_type_id").references(MessengerTypes) { it.messengerType }
    val repositoryId = int("repository_id").references(Repositories) { it.repository }
}