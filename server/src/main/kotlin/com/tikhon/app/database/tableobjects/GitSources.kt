package com.tikhon.app.database.tableobjects

import com.tikhon.app.database.tableobjects.entities.GitSource
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object GitSources : Table<GitSource>("t_git_sources") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val domain = varchar("domain").bindTo { it.domain }
}