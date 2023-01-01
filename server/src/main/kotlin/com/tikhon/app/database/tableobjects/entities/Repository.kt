package com.tikhon.app.database.tableobjects.entities

import org.ktorm.entity.Entity

interface Repository : Entity<Repository> {
    val id: Int
    val fullName: String
    val shortName: String
    val gitSource: GitSource
}