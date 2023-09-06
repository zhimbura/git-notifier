package com.tikhon.app.adapters.git.gitlab

import com.tikhon.app.adapters.git.GitAdapter
import com.tikhon.app.adapters.git.gitlab.dto.MergeRequestHook
import com.tikhon.app.adapters.git.gitlab.dto.ObjectKind
import com.tikhon.app.adapters.git.gitlab.dto.ObjectKindType
import com.tikhon.app.adapters.git.gitlab.dto.PipelineHook
import com.tikhon.app.events.IGitEvent
import com.tikhon.app.events.dto.git.*
import kotlinx.serialization.json.Json

// TODO Переделать на аннотации
// Типа такого @GitAdapter("GitLab")
object GitLabAdapter : GitAdapter("GitLab") {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

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
        val mergeRequestHook = json.decodeFromString(MergeRequestHook.serializer(), data)
        return IGitEvent.MergeRequestEvent(
            gitSource = GitSource.fromURL(mergeRequestHook.project.webUrl),
            project = GitProject(
                name = mergeRequestHook.project.name,
                namespace = mergeRequestHook.project.namespace,
                pathWithNameSpace = mergeRequestHook.project.pathWithNamespace,
            ),
            mergeRequest = GitMergeRequest(
                title = mergeRequestHook.attributes.title,
                description = mergeRequestHook.attributes.description,
                url = mergeRequestHook.attributes.url,
                action = GitMergeRequestAction.fromString(mergeRequestHook.attributes.action),
                sourceBranch = mergeRequestHook.attributes.sourceBranch,
                targetBranch = mergeRequestHook.attributes.targetBranch,
                author = mergeRequestHook.user.let { GitUser(it.name, it.username) },
                reviewers = mergeRequestHook.reviewers.map { GitUser(it.name, it.username) }
            )
        )
    }
}