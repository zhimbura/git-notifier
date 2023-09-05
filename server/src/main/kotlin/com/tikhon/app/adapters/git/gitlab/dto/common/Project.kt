package com.tikhon.app.adapters.git.gitlab.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int,
    val name: String,
    val description: String = "",
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