package com.tikhon.app.adapters.git.gitlab.dto

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PipelineHook(
    @property:SerialName("object_attributes")
    val objectAttributes: ObjectAttributes,
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
data class ObjectAttributes(
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
    val finishedAt: String,
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
    val detailedMergeStatus: String,
    val url: String
)

@Serializable
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val avatar_url: String,
    val email: String
)

@Serializable
data class Project(
    val id: Int,
    val name: String,
    val description: String,
    @property:SerialName("web_url")
    val webUrl: String,
    @property:SerialName("avatar_url")
    val avatarURL: String? = null,
    @property:SerialName("git_ssh_url")
    val gitSHH: String,
    @property:SerialName("git_http_url")
    val gitHTTP: String,
    val namespace: String,
    @property:SerialName("visibility_level")
    val visibilityLevel: Int,
    @property:SerialName("path_with_namespace")
    val pathWithNamespace: String,
    @property:SerialName("default_branch")
    val defaultBranch: String
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
)