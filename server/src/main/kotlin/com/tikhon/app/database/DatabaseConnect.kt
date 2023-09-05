package com.tikhon.app.database

import com.tikhon.app.adapters.messenger.MessengerType
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

    fun getMessagesByRepository(gitSource: String, pathWithNameSpace: String): Map<MessengerType, Set<String>>? {
        // TODO Оптимизировать запросы
        val sourceId = getGetSourceIdBySource(gitSource)
        if (sourceId == null) {
            println("Не найден Git источник")
            return null// TODO Возможно нужно кинуть ошибку ?
        }
        val repositoryId = getRepositoryId(sourceId, pathWithNameSpace)
        if (repositoryId == null) {
            println("Не найден репозиторий")
            return null// TODO Возможно нужно кинуть ошибку ?
        }
        val subscribers = getSubscribes(repositoryId)
        if (subscribers.isEmpty()) {
            println("Не найдены подписки")
            return null// TODO Возможно нужно кинуть ошибку ?
        }
        val messengers = subscribers.map { it.second }.toSet()
        val messengerTypes = messengers.flatMap { messengerId ->
            db
                .from(MessengerTypes)
                .select(MessengerTypes.name)
                .where { MessengerTypes.id eq messengerId }
                .mapNotNull { messengerId to it[MessengerTypes.name] }
        }.toMap()
        val groupedByMessenger: LinkedHashMap<MessengerType, LinkedHashSet<String>> = linkedMapOf()
        for ((chatId, messengerId) in subscribers) {
            val messengerType = messengerTypes[messengerId]?.let { MessengerType.getOrNull(it) }
            check(messengerType != null) { "Ошибка данных" }
            groupedByMessenger
                .getOrPut(messengerType) { linkedSetOf() }
                .add(chatId)
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

    fun getGetSourceIdBySource(source: String): Int? {
        return db.from(GitSources)
            .select(GitSources.id)
            .where { GitSources.source eq source }
            .mapNotNull { it[GitSources.id] }
            .firstOrNull()
    }

    fun addGitSource(name: String, source: String): Int {
        return getGetSourceIdBySource(source) ?: db.insertAndGenerateKey(GitSources) {
            set(GitSources.name, name)
            set(GitSources.source, source)
        } as Int
    }


    fun getRepositoryId(sourceId: Int, pathWithNameSpace: String): Int? {
        return db
            .from(Repositories)
            .select(Repositories.id)
            .where { (Repositories.fullName eq pathWithNameSpace) and (Repositories.gitSourceId eq sourceId) }
            .mapNotNull { it[Repositories.id] }
            .firstOrNull()
    }

    fun addRepository(fullName: String, shortName: String, gitSourceId: Int): Int {
        return getRepositoryId(gitSourceId, fullName) ?: db.insertAndGenerateKey(Repositories) {
            set(Repositories.fullName, fullName)
            set(Repositories.shortName, shortName)
            set(Repositories.gitSourceId, gitSourceId)
        } as Int
    }


    fun getMessengerId(type: MessengerType): Int? {
        return db
            .from(MessengerTypes)
            .select(MessengerTypes.id)
            .where { MessengerTypes.name eq type.type }
            .mapNotNull { it[MessengerTypes.id] }
            .firstOrNull()
    }

    fun addMessengerType(messengerName: MessengerType): Int {
        return getMessengerId(messengerName) ?: db.insertAndGenerateKey(MessengerTypes) {
            set(MessengerTypes.name, messengerName.type)
        } as Int
    }

    fun getSubscribes(repositoryId: Int): Set<Pair<String, Int>> {
        return db
            .from(RepositorySubscribes)
            .select(listOf(RepositorySubscribes.chatId, RepositorySubscribes.messengerTypeId))
            .where { RepositorySubscribes.repositoryId eq repositoryId }
            .mapNotNull {
                if (it[RepositorySubscribes.chatId] == null || it[RepositorySubscribes.messengerTypeId] == null) null
                else it[RepositorySubscribes.chatId]!! to it[RepositorySubscribes.messengerTypeId]!!
            }
            .toSet()
    }

    fun addSubscribe(chatId: String, messengerId: Int, repositoryId: Int): ResultType {
        val countSubscribe = db
            .from(RepositorySubscribes)
            .select(RepositorySubscribes.id)
            .where {
                (RepositorySubscribes.repositoryId eq repositoryId) and
                (RepositorySubscribes.messengerTypeId eq messengerId) and
                (RepositorySubscribes.chatId eq chatId)
            }.totalRecords
        if (countSubscribe != 0) {
            return ResultType.NO_CHANGE
        }
        return asResultType {
            db.insert(RepositorySubscribes) {
                set(RepositorySubscribes.chatId, chatId)
                set(RepositorySubscribes.messengerTypeId, messengerId)
                set(RepositorySubscribes.repositoryId, repositoryId)
            }
        }
    }

    fun addAlias(gitLogin: String, messengerLogin: String, gitSourceId: Int, messengerTypeId: Int): ResultType {
        val existAliasesByGit = db
            .from(Aliases)
            .select(listOf(Aliases.id, Aliases.messengerLogin, Aliases.messengerTypeId))
            .where {
                (Aliases.gitLogin eq gitLogin) and (Aliases.gitSourceId eq gitSourceId)
            }
            .mapNotNull {
                Triple(
                    it[Aliases.id]!!,
                    it[Aliases.messengerLogin]!!,
                    it[Aliases.messengerTypeId]!!
                )
            }
        val hasExistAlias = existAliasesByGit.find { it.third == messengerTypeId }
        return when {
            hasExistAlias == null -> {
                asResultType {
                    db.insert(Aliases) {
                        set(Aliases.gitLogin, gitLogin)
                        set(Aliases.gitSourceId, gitSourceId)
                        set(Aliases.messengerLogin, messengerLogin)
                        set(Aliases.messengerTypeId, messengerTypeId)
                    }
                }
            }
            hasExistAlias.second != messengerLogin -> {
                asResultType {
                    db.update(Aliases) {
                        set(Aliases.messengerLogin, messengerLogin)
                        where { it.id eq hasExistAlias.first }
                    }
                }
            }
            else -> ResultType.NO_CHANGE
        }
    }

    private fun asResultType(f: () -> Int): ResultType {
        return try {
            val affectedRows = f()
            if (affectedRows > 0) {
                ResultType.SUCCESS
            } else {
                ResultType.NO_CHANGE
            }
        } catch (err: Exception) {
            err.printStackTrace()
            ResultType.ERROR
        }
    }

    fun getAlias(gitLogin: String, gitSource: String, messengerType: MessengerType): String? {
        val gitSourceId = getGetSourceIdBySource(gitSource) ?: return null
        val messengerId = getMessengerId(messengerType) ?: return null
        return db
            .from(Aliases)
            .select(Aliases.messengerLogin)
            .where {
                (Aliases.gitLogin eq gitLogin) and
                (Aliases.gitSourceId eq gitSourceId) and
                (Aliases.messengerTypeId eq messengerId)
            }
            .mapNotNull { it[Aliases.messengerLogin] }
            .firstOrNull()
    }
}