package com.tikhon.app.database.tableobjects.entities

import org.ktorm.entity.Entity

interface RepositorySubscriber: Entity<RepositorySubscriber> {
    val id: Int
    val chatId: String
    val messengerType: MessengerType
    val repository: Repository
}