package com.tikhon.app.database

import com.tikhon.plugins.getLogger
import io.ktor.util.logging.*
import tech.ydb.common.transaction.TxMode
import tech.ydb.core.auth.EnvironAuthProvider
import tech.ydb.core.auth.NopAuthProvider
import tech.ydb.core.impl.auth.GrpcAuthRpc
import tech.ydb.core.grpc.GrpcTransport
import tech.ydb.table.SessionRetryContext
import tech.ydb.table.TableClient
import tech.ydb.table.query.Params
import tech.ydb.table.result.ResultSetReader
import tech.ydb.table.transaction.TxControl
import tech.ydb.table.values.PrimitiveValue
import tech.ydb.core.Result
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val YDB_CONNECTION_STRING = System.getenv("YDB_CONNECTION_STRING")
    ?: "grpc://localhost:2136/local"

class DatabaseConnect {
    private val logger = getLogger(this::class.java)

    private val transport = runCatching {
        @Suppress("UNCHECKED_CAST")
        val authProvider = (if (YDB_CONNECTION_STRING.startsWith("grpcs://")) EnvironAuthProvider()
            else NopAuthProvider()) as tech.ydb.auth.AuthRpcProvider<GrpcAuthRpc>
        GrpcTransport.forConnectionString(YDB_CONNECTION_STRING)
            .withAuthProvider(authProvider)
            .build()
    }.onFailure { logger.error(it) }.getOrNull()
        ?: throw IllegalStateException("Не удалось установить подключение к YDB")

    private val tableClient = TableClient.newClient(transport).build()
    private val retryCtx = SessionRetryContext.create(tableClient).build()

    private val tablePathPrefix: String = transport.database.let { if (it.endsWith("/")) it else "$it/" }

    private fun tablePath(name: String) = tablePathPrefix + name

    fun checkConnect() {
        require(isValidTables()) { "Ошибка инициализации подключения к базе данных YDB" }
    }

    fun getMessengersByRepository(gitSource: String, pathWithNameSpace: String): Map<String, Set<String>>? {
        val sourceId = getGetSourceIdBySource(gitSource) ?: run {
            logger.warn("Не найден Git источник: $gitSource")
            return null
        }
        val repositoryId = getRepositoryId(sourceId, pathWithNameSpace) ?: run {
            logger.warn("Не найден репозиторий: $pathWithNameSpace")
            return null
        }
        val subscribers = getSubscribes(repositoryId)
        if (subscribers.isEmpty()) {
            logger.warn("Не найдены подписки для repositoryId=$repositoryId")
            return null
        }
        val messengerTypes = subscribers.map { it.second }.toSet()
        val messengerNames = mutableMapOf<Int, String>()
        for (messengerId in messengerTypes) {
            val name = getMessengerNameById(messengerId) ?: continue
            messengerNames[messengerId] = name
        }
        val groupedByMessenger: LinkedHashMap<String, LinkedHashSet<String>> = linkedMapOf()
        for ((chatId, messengerId) in subscribers) {
            val messengerType = messengerNames[messengerId] ?: continue
            groupedByMessenger.getOrPut(messengerType) { linkedSetOf() }.add(chatId)
        }
        return groupedByMessenger
    }

    private fun isValidTables(): Boolean = try {
        retryCtx.supplyResult { session ->
            session.executeDataQuery(
                "SELECT 1 FROM ${tablePath("t_messenger_type")} LIMIT 1",
                TxControl.onlineRo(),
                Params.empty()
            )
        }.join().isSuccess
    } catch (e: Exception) {
        logger.error(e)
        false
    }

    private fun getNextId(tableName: String): Long = retryCtx.supplyResult { session ->
        val tx = session.createNewTransaction(TxMode.SERIALIZABLE_RW)
        val selectQuery = """
            DECLARE ${'$'}table_name AS Utf8;
            SELECT next_id FROM ${tablePath("id_sequences")} WHERE table_name = ${'$'}table_name;
        """.trimIndent()
        val selectRes = tx.executeDataQuery(selectQuery, Params.of("\$table_name", PrimitiveValue.newText(tableName))).join()
        if (!selectRes.isSuccess) throw IllegalStateException("SELECT next_id failed: ${selectRes.status}")
        val rs = selectRes.value.getResultSet(0)
        val nextId = if (rs.next()) rs.getColumn("next_id").getInt64() else 1L
        val newVal = nextId + 1
        val updateQuery = """
            DECLARE ${'$'}table_name AS Utf8;
            DECLARE ${'$'}new_val AS Int64;
            UPDATE ${tablePath("id_sequences")} SET next_id = ${'$'}new_val WHERE table_name = ${'$'}table_name;
        """.trimIndent()
        tx.executeDataQuery(updateQuery, Params.of("\$table_name", PrimitiveValue.newText(tableName), "\$new_val", PrimitiveValue.newInt64(newVal))).join()
        tx.commit().join()
        CompletableFuture.completedFuture(Result.success(nextId))
    }.join().value

    private fun getMessengerNameById(id: Int): String? = runQuery(
        "DECLARE ${'$'}id AS Int64; SELECT name FROM ${tablePath("t_messenger_type")} WHERE id = ${'$'}id;",
        Params.of("\$id", PrimitiveValue.newInt64(id.toLong()))
    ) { rs -> if (rs.next()) rs.getColumn("name").getText() else null }

    fun getGetSourceIdBySource(source: String): Int? = runQuery(
        "DECLARE ${'$'}source AS Utf8; SELECT id FROM ${tablePath("t_git_sources")} WHERE source = ${'$'}source;",
        Params.of("\$source", PrimitiveValue.newText(source))
    ) { rs -> if (rs.next()) rs.getColumn("id").getInt64().toInt() else null }

    fun addGitSource(name: String, source: String): Int {
        getGetSourceIdBySource(source)?.let { return it }
        val id = getNextId("t_git_sources").toInt()
        retryCtx.supplyStatus { session ->
            session.executeDataQuery(
                """
                DECLARE ${'$'}id AS Int64;
                DECLARE ${'$'}name AS Utf8;
                DECLARE ${'$'}source AS Utf8;
                INSERT INTO ${tablePath("t_git_sources")} (id, name, source) VALUES (${'$'}id, ${'$'}name, ${'$'}source);
                """.trimIndent(),
                TxControl.serializableRw().setCommitTx(true),
                Params.of("\$id", PrimitiveValue.newInt64(id.toLong()), "\$name", PrimitiveValue.newText(name), "\$source", PrimitiveValue.newText(source))
            ).thenApply { it.status }
        }.join().expectSuccess("addGitSource")
        return id
    }

    fun getRepositoryId(sourceId: Int, pathWithNameSpace: String): Int? = runQuery(
        """
        DECLARE ${'$'}full_name AS Utf8;
        DECLARE ${'$'}git_source_id AS Int64;
        SELECT id FROM ${tablePath("t_repositories")} WHERE full_name = ${'$'}full_name AND git_source_id = ${'$'}git_source_id;
        """.trimIndent(),
        Params.of("\$full_name", PrimitiveValue.newText(pathWithNameSpace), "\$git_source_id", PrimitiveValue.newInt64(sourceId.toLong()))
    ) { rs -> if (rs.next()) rs.getColumn("id").getInt64().toInt() else null }

    fun addRepository(fullName: String, shortName: String, gitSourceId: Int): Int {
        getRepositoryId(gitSourceId, fullName)?.let { return it }
        val id = getNextId("t_repositories").toInt()
        retryCtx.supplyStatus { session ->
            session.executeDataQuery(
                """
                DECLARE ${'$'}id AS Int64;
                DECLARE ${'$'}full_name AS Utf8;
                DECLARE ${'$'}short_name AS Utf8;
                DECLARE ${'$'}git_source_id AS Int64;
                INSERT INTO ${tablePath("t_repositories")} (id, full_name, short_name, git_source_id) VALUES (${'$'}id, ${'$'}full_name, ${'$'}short_name, ${'$'}git_source_id);
                """.trimIndent(),
                TxControl.serializableRw().setCommitTx(true),
                Params.of("\$id", PrimitiveValue.newInt64(id.toLong()), "\$full_name", PrimitiveValue.newText(fullName), "\$short_name", PrimitiveValue.newText(shortName), "\$git_source_id", PrimitiveValue.newInt64(gitSourceId.toLong()))
            ).thenApply { it.status }
        }.join().expectSuccess("addRepository")
        return id
    }

    fun getMessengerId(type: String): Int? = runQuery(
        "DECLARE ${'$'}name AS Utf8; SELECT id FROM ${tablePath("t_messenger_type")} WHERE name = ${'$'}name;",
        Params.of("\$name", PrimitiveValue.newText(type))
    ) { rs -> if (rs.next()) rs.getColumn("id").getInt64().toInt() else null }

    fun addMessengerType(messengerName: String): Int {
        getMessengerId(messengerName)?.let { return it }
        val id = getNextId("t_messenger_type").toInt()
        retryCtx.supplyStatus { session ->
            session.executeDataQuery(
                """
                DECLARE ${'$'}id AS Int64;
                DECLARE ${'$'}name AS Utf8;
                INSERT INTO ${tablePath("t_messenger_type")} (id, name) VALUES (${'$'}id, ${'$'}name);
                """.trimIndent(),
                TxControl.serializableRw().setCommitTx(true),
                Params.of("\$id", PrimitiveValue.newInt64(id.toLong()), "\$name", PrimitiveValue.newText(messengerName))
            ).thenApply { it.status }
        }.join().expectSuccess("addMessengerType")
        return id
    }

    fun getSubscribes(repositoryId: Int): Set<Pair<String, Int>> = runQuery(
        "DECLARE ${'$'}repository_id AS Int64; SELECT chat_id, messenger_type_id FROM ${tablePath("t_repository_subscribes")} WHERE repository_id = ${'$'}repository_id;",
        Params.of("\$repository_id", PrimitiveValue.newInt64(repositoryId.toLong()))
    ) { rs ->
        val set = mutableSetOf<Pair<String, Int>>()
        while (rs.next()) {
            set.add(rs.getColumn("chat_id").getText() to rs.getColumn("messenger_type_id").getInt64().toInt())
        }
        set
    } ?: emptySet()

    fun addSubscribe(chatId: String, messengerId: Int, repositoryId: Int): ResultType {
        val count = runQuery(
            """
            DECLARE ${'$'}repository_id AS Int64;
            DECLARE ${'$'}messenger_type_id AS Int64;
            DECLARE ${'$'}chat_id AS Utf8;
            SELECT COUNT(*) AS cnt FROM ${tablePath("t_repository_subscribes")} WHERE repository_id = ${'$'}repository_id AND messenger_type_id = ${'$'}messenger_type_id AND chat_id = ${'$'}chat_id;
            """.trimIndent(),
            Params.of("\$repository_id", PrimitiveValue.newInt64(repositoryId.toLong()), "\$messenger_type_id", PrimitiveValue.newInt64(messengerId.toLong()), "\$chat_id", PrimitiveValue.newText(chatId))
        ) { rs -> if (rs.next()) rs.getColumn("cnt").getUint64().toInt() else 0 } ?: 0
        if (count != 0) return ResultType.NO_CHANGE
        val id = getNextId("t_repository_subscribes").toInt()
        return try {
            val status = retryCtx.supplyStatus { session ->
                session.executeDataQuery(
                    """
                    DECLARE ${'$'}id AS Int64;
                    DECLARE ${'$'}chat_id AS Utf8;
                    DECLARE ${'$'}messenger_type_id AS Int64;
                    DECLARE ${'$'}repository_id AS Int64;
                    INSERT INTO ${tablePath("t_repository_subscribes")} (id, chat_id, messenger_type_id, repository_id) VALUES (${'$'}id, ${'$'}chat_id, ${'$'}messenger_type_id, ${'$'}repository_id);
                    """.trimIndent(),
                    TxControl.serializableRw().setCommitTx(true),
                    Params.of("\$id", PrimitiveValue.newInt64(id.toLong()), "\$chat_id", PrimitiveValue.newText(chatId), "\$messenger_type_id", PrimitiveValue.newInt64(messengerId.toLong()), "\$repository_id", PrimitiveValue.newInt64(repositoryId.toLong()))
                ).thenApply { it.status }
            }.join()
            if (status.isSuccess) ResultType.SUCCESS else ResultType.ERROR
        } catch (e: Exception) {
            logger.error(e)
            ResultType.ERROR
        }
    }

    fun addAlias(gitLogin: String, messengerLogin: String, gitSourceId: Int, messengerTypeId: Int): ResultType {
        val existAliases = runQuery(
            """
            DECLARE ${'$'}git_login AS Utf8;
            DECLARE ${'$'}git_source_id AS Int64;
            SELECT id, messenger_login, messenger_type_id FROM ${tablePath("t_aliases")} WHERE git_login = ${'$'}git_login AND git_source_id = ${'$'}git_source_id;
            """.trimIndent(),
            Params.of("\$git_login", PrimitiveValue.newText(gitLogin), "\$git_source_id", PrimitiveValue.newInt64(gitSourceId.toLong()))
        ) { rs ->
            val list = mutableListOf<Triple<Long, String, Int>>()
            while (rs.next()) list.add(Triple(rs.getColumn("id").getInt64(), rs.getColumn("messenger_login").getText(), rs.getColumn("messenger_type_id").getInt64().toInt()))
            list
        } ?: emptyList()
        val hasExistAlias = existAliases.find { it.third == messengerTypeId }
        return when {
            hasExistAlias == null -> asResultType {
                val id = getNextId("t_aliases").toInt()
                retryCtx.supplyStatus { session ->
                    session.executeDataQuery(
                        """
                        DECLARE ${'$'}id AS Int64;
                        DECLARE ${'$'}git_login AS Utf8;
                        DECLARE ${'$'}messenger_login AS Utf8;
                        DECLARE ${'$'}git_source_id AS Int64;
                        DECLARE ${'$'}messenger_type_id AS Int64;
                        INSERT INTO ${tablePath("t_aliases")} (id, git_login, messenger_login, git_source_id, messenger_type_id) VALUES (${'$'}id, ${'$'}git_login, ${'$'}messenger_login, ${'$'}git_source_id, ${'$'}messenger_type_id);
                        """.trimIndent(),
                        TxControl.serializableRw().setCommitTx(true),
                        Params.of("\$id", PrimitiveValue.newInt64(id.toLong()), "\$git_login", PrimitiveValue.newText(gitLogin), "\$messenger_login", PrimitiveValue.newText(messengerLogin), "\$git_source_id", PrimitiveValue.newInt64(gitSourceId.toLong()), "\$messenger_type_id", PrimitiveValue.newInt64(messengerTypeId.toLong()))
                    ).thenApply { it.status }
                }.join()
            }
            hasExistAlias.second != messengerLogin -> asResultType {
                retryCtx.supplyStatus { session ->
                    session.executeDataQuery(
                        """
                        DECLARE ${'$'}id AS Int64;
                        DECLARE ${'$'}messenger_login AS Utf8;
                        UPDATE ${tablePath("t_aliases")} SET messenger_login = ${'$'}messenger_login WHERE id = ${'$'}id;
                        """.trimIndent(),
                        TxControl.serializableRw().setCommitTx(true),
                        Params.of("\$id", PrimitiveValue.newInt64(hasExistAlias.first), "\$messenger_login", PrimitiveValue.newText(messengerLogin))
                    ).thenApply { it.status }
                }.join()
            }
            else -> ResultType.NO_CHANGE
        }
    }

    private fun asResultType(f: () -> tech.ydb.core.Status): ResultType = try {
        val status = f()
        if (status.isSuccess) ResultType.SUCCESS else ResultType.NO_CHANGE
    } catch (e: Exception) {
        logger.error(e)
        ResultType.ERROR
    }

    fun getAlias(gitLogin: String, gitSource: String, messengerType: String): String? {
        val gitSourceId = getGetSourceIdBySource(gitSource) ?: return null
        val messengerId = getMessengerId(messengerType) ?: return null
        return runQuery(
            """
            DECLARE ${'$'}git_login AS Utf8;
            DECLARE ${'$'}git_source_id AS Int64;
            DECLARE ${'$'}messenger_type_id AS Int64;
            SELECT messenger_login FROM ${tablePath("t_aliases")} WHERE git_login = ${'$'}git_login AND git_source_id = ${'$'}git_source_id AND messenger_type_id = ${'$'}messenger_type_id;
            """.trimIndent(),
            Params.of("\$git_login", PrimitiveValue.newText(gitLogin), "\$git_source_id", PrimitiveValue.newInt64(gitSourceId.toLong()), "\$messenger_type_id", PrimitiveValue.newInt64(messengerId.toLong()))
        ) { rs -> if (rs.next()) rs.getColumn("messenger_login").getText() else null }
    }

    private fun <T> runQuery(query: String, params: Params, block: (ResultSetReader) -> T): T? = try {
        val result = retryCtx.supplyResult { session ->
            session.executeDataQuery(query, TxControl.onlineRo(), params)
        }.get(10, TimeUnit.SECONDS)
        if (!result.isSuccess) null else block(result.value.getResultSet(0))
    } catch (e: Exception) {
        logger.error(e)
        null
    }

    fun close() {
        tableClient.close()
        transport.close()
    }
}
