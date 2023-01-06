package com.tikhon.app.database.tableobjects.entities

import org.ktorm.entity.Entity

interface GitSource : Entity<GitSource> {
    val id: Int
    val name: String
    val source: String
}