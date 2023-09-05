package com.tikhon.app.adapters.git.gitlab.dto

import com.tikhon.app.adapters.git.gitlab.dto.common.Project
import com.tikhon.app.adapters.git.gitlab.dto.common.User
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PipelineHook(
    @property:SerialName("object_attributes")
    val objectAttributes: PipelineHookAttributes,
    @property:SerialName("merge_request")
    val mergeRequest: MergeRequest? = null,
    val user: User,
    val project: Project,
    val commit: Commit,
    @property:SerialName("source_pipeline")
    val sourcePipeline: SourcePipeline? = null,
    val builds: List<BuildStage>
)


@Serializable
data class PipelineHookAttributes(
    val id: Int,
    val iid: Int? = null,
    val ref: String,
    val tag: Boolean,
    val sha: String,
    @property:SerialName("before_sha")
    val beforeSHA: String,
    val source: String,
    val status: String, // TODO Если будет завязка на успешность то сделать enum
    val stages: List<String>,
    @property:SerialName("created_at")
    val createdAt: String,
    @property:SerialName("finished_at")
    val finishedAt: String? = null,
    val duration: Float? = null,
    val variables: List<Variable>? = emptyList()
)

@Serializable
data class Variable(
    val key: String,
    val value: String
)

@Serializable
data class MergeRequest(
    val id: Int,
    val iid: Int? = null,
    val title: String,
    @property:SerialName("source_branch")
    val sourceBranch: String,
    @property:SerialName("source_project_id")
    val sourceProjectId: Int,
    @property:SerialName("target_branch")
    val targetBranch: String,
    @property:SerialName("target_project_id")
    val targetProjectId: Int,
    val state: String,
    @property:SerialName("merge_status")
    val mergeStatus: String,
    @property:SerialName("detailed_merge_status")
    val detailedMergeStatus: String? = null,
    val url: String
)

@Serializable
data class Commit(
    val id: String,
    val message: String,
    val timestamp: String,
    val url: String,
    val author: Author
)

@Serializable
data class Author(
    val name: String,
    val email: String
)

@Serializable
data class SourcePipeline(
    val project: ProjectReference,
    @property:SerialName("pipeline_id")
    val pipelineId: Int,
    @property:SerialName("job_id")
    val jobId: Int
)

@Serializable
data class ProjectReference(
    val id: Int,
    @property:SerialName("web_url")
    val webURL: String,
    @property:SerialName("path_with_namespace")
    val pathWithNamespace: String
)

@Serializable
data class BuildStage(
    val id: Int,
    val stage: String,
    val name: String,
    val status: String,
    @property:SerialName("created_at")
    val createdAt: String,
    @property:SerialName("started_at")
    val startedAt: String? = null,
    @property:SerialName("finished_at")
    val finishedAt: String? = null,
    val duration: Float? = null,
    @property:SerialName("queued_duration")
    val queuedDuration: Float? = null,
    @property:SerialName("failure_reason")
    val failureReason: String? = null,
    @property:SerialName("when")
    val type: String,
    val manual: Boolean,
    @property:SerialName("allow_failure")
    val allowFailure: Boolean,
    val user: User,
)