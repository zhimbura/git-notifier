package com.tikhon.app.adapters.git.gitlab.dto

import com.tikhon.app.adapters.git.gitlab.dto.common.Project
import com.tikhon.app.adapters.git.gitlab.dto.common.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MergeRequestHook(
    val user: User,
    val project: Project,
    @property:SerialName("object_attributes")
    val attributes: MergeRequestHookAttributes,
    val reviewers: List<User> = emptyList()
)

@Serializable
data class MergeRequestHookAttributes(
    val id: Int,
    val iid: Int? = null,
    @property:SerialName("target_branch")
    val targetBranch: String,
    @property:SerialName("source_branch")
    val sourceBranch: String,
    val title: String,
    val description: String = "",
    val url: String,
    val action: String = "open"
)