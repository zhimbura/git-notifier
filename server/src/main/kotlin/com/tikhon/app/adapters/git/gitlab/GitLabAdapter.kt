package com.tikhon.app.adapters.git.gitlab

import com.tikhon.app.adapters.git.GitAdapter
import com.tikhon.app.adapters.git.gitlab.dto.ObjectKind
import com.tikhon.app.adapters.git.gitlab.dto.ObjectKindType
import com.tikhon.app.adapters.git.gitlab.dto.PipelineHook
import com.tikhon.app.events.IGitEvent
import com.tikhon.app.events.dto.git.GitProject
import com.tikhon.app.events.dto.git.GitSource
import com.tikhon.app.events.dto.git.GitUser
import com.tikhon.app.events.dto.git.Status
import kotlinx.serialization.json.Json

// TODO Переделать на аннотации
// Типа такого @GitAdapter("GitLab")
object GitLabAdapter : GitAdapter("GitLab") {
    private val json = Json { ignoreUnknownKeys = true }

    override fun parseEvent(data: String): IGitEvent? {
        val kind = try {
            json.decodeFromString(ObjectKind.serializer(), data)
        } catch (err: Exception) {
            err.printStackTrace() // TODO Переделать на логгер
            return null
        }
        return when (kind.objectKind) {
          ObjectKindType.PIPELINE -> parsePipeline(data)
          ObjectKindType.MERGE_REQUEST -> parseMergeRequest(data)
          else -> null // TODO logger("Не поддерживается")
        }
    }

    private fun parsePipeline(data: String): IGitEvent {
        val pipelineHook = json.decodeFromString(PipelineHook.serializer(), data)
        return IGitEvent.PipelineEvent(
            gitSource = GitSource.fromURL(pipelineHook.project.webUrl),
            status = Status.valueOf(pipelineHook.objectAttributes.status.uppercase()),
            project = GitProject(
                pipelineHook.project.name,
                pipelineHook.project.namespace,
                pipelineHook.project.pathWithNamespace,
            ),
            branch = pipelineHook.objectAttributes.ref,
            users = pipelineHook.builds.map { GitUser(it.user.name, it.user.username) }.distinct(),
            lastCommitMessage = pipelineHook.commit.message,
        )
    }

    private fun parseMergeRequest(data: String): IGitEvent {
        TODO("Сформировать и кинуть события в ApplicationCore")
    }
}