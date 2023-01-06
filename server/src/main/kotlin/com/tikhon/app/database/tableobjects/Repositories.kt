package com.tikhon.app.database.tableobjects

import com.tikhon.app.database.tableobjects.entities.Repository
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Repositories : Table<Repository>("t_repositories") {
    val id = int("id").primaryKey().bindTo { it.id }
    val fullName = varchar("full_name").bindTo { it.fullName }
    val shortName = varchar("short_name").bindTo { it.shortName }
    val gitSourceId = int("git_source_id").references(GitSources) { it.gitSource }
}