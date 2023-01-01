package com.tikhon.app.database

import com.tikhon.app.database.tableobjects.Repositories
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.mapNotNull
import org.ktorm.dsl.select

private val DB_HOST = System.getenv("DB_HOST")
private val DB_PORT = System.getenv("DB_PORT")
private val DB_USER = System.getenv("DB_USER")
private val DB_PASSWORD = System.getenv("DB_PASSWORD")
private val DB_SCHEMA = System.getenv("DB_SCHEMA")

class DatabaseConnect {
    private val db = Database.connect("jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_SCHEMA?currentSchema=$DB_SCHEMA&user=$DB_USER&password=$DB_PASSWORD")


    fun getProjects(): List<String> {
        return db.from(Repositories).select().mapNotNull { row -> row[Repositories.shortName] }
    }
}