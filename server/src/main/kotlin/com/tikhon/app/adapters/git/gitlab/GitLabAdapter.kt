package com.tikhon.app.adapters.git.gitlab

import com.tikhon.app.adapters.git.GitAdapter
import com.tikhon.app.adapters.git.gitlab.dto.ObjectKind
import com.tikhon.app.adapters.git.gitlab.dto.ObjectKindType
import com.tikhon.app.adapters.git.gitlab.dto.PipelineHook
import com.tikhon.app.events.IGitEvent
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
          else -> null
        }
    }

    private fun parsePipeline(data: String): IGitEvent {
        val pipelineHook = json.decodeFromString(PipelineHook.serializer(), data)
        TODO()
    }

    private fun parseMergeRequest(data: String): IGitEvent {
        TODO()
    }
}