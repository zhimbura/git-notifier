package com.tikhon.app.database

import com.tikhon.app.database.tableobjects.*
import org.ktorm.database.Database
import org.ktorm.dsl.*

private val DB_HOST = System.getenv("DB_HOST")
private val DB_PORT = System.getenv("DB_PORT")
private val DB_USER = System.getenv("DB_USER")
private val DB_PASSWORD = System.getenv("DB_PASSWORD")
private val DB_NAME = System.getenv("DB_NAME")
private val DB_SCHEMA = System.getenv("DB_SCHEMA")

class DatabaseConnect {
    private val db = Database.connect(
        "jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME?currentSchema=${DB_SCHEMA}&user=$DB_USER&password=$DB_PASSWORD"
    )

    fun checkConnect() {
        require(isValidTables()) { "Ошибка инициализации подключения к базе данных" }
    }

    private fun isValidTables(): Boolean {
        // TODO может есть какой-то более правильный способ проверить что все таблицы на месте и описание в приложении совпадает с базой
        return try {
            db.from(Aliases).select().mapNotNull { it.toString() }
            db.from(GitSources).select().mapNotNull { it.toString() }
            db.from(MessengerTypes).select().mapNotNull { it.toString() }
            db.from(Repositories).select().mapNotNull { it.toString() }
            db.from(RepositorySubscribes).select().mapNotNull { it.toString() }
            true
        } catch (err: Exception) {
            err.printStackTrace() // TODO Переделать на логгер
            false
        }
    }
}