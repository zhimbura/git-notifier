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

    fun getMessagesByRepository(gitSource: String, pathWithNameSpace: String): Map<String, Set<String>>? {
        // TODO Оптимизировать запросы
        val source = db
            .from(GitSources)
            .select(GitSources.id)
            .where { GitSources.name eq gitSource }
            .mapNotNull { it[GitSources.id] }
        if (source.isEmpty() || source.size > 1) {
            println("Не найден Git источник")
            return null// TODO Возможно нужно кинуть ошибку ?
        }
        val repository = db
            .from(Repositories)
            .select(Repositories.id)
            .where { (Repositories.fullName eq pathWithNameSpace) and (Repositories.gitSourceId eq source.first()) }
            .mapNotNull { it[Repositories.id] }
        if (repository.isEmpty() || repository.size > 1) {
            println("Не найден репозиторий")
            return null// TODO Возможно нужно кинуть ошибку ?
        }
        val subscribers = db
            .from(RepositorySubscribes)
            .select(listOf(RepositorySubscribes.chatId, RepositorySubscribes.messengerTypeId))
            .where{ RepositorySubscribes.repositoryId eq repository.first() }
            .map { it[RepositorySubscribes.chatId] to it[RepositorySubscribes.messengerTypeId] }
        if (repository.isEmpty() || repository.size > 1) {
            println("Не найдены подписки")
            return null// TODO Возможно нужно кинуть ошибку ?
        }
        val messengers = subscribers.mapNotNull { it.second }.toSet()
        val messengerTypes = messengers.flatMap { messengerId ->
            db
                .from(MessengerTypes)
                .select(MessengerTypes.name)
                .where{ MessengerTypes.id eq messengerId}
                .mapNotNull { messengerId to it[MessengerTypes.name] }
        }.toMap()
        val groupedByMessenger: LinkedHashMap<String, LinkedHashSet<String>> = linkedMapOf()
        for ((chatId, messengerId) in subscribers) {
            if (chatId != null && messengerId != null) {
                val messengerType = messengerTypes[messengerId]
                check(messengerType != null) { "Ошибка данных" }
                groupedByMessenger
                    .getOrPut(messengerType) { linkedSetOf() }
                    .add(chatId)
            } else {
                println("Не найден тип мессенджера")
                throw Exception("Ошибка данных")
            }
        }
        return groupedByMessenger
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