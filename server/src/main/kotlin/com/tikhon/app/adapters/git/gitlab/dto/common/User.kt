package com.tikhon.app.adapters.git.gitlab.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val username: String,
    @property:SerialName("avatar_url")
    val avatarUrl: String,
    val email: String? = null
)