package com.tikhon.app.adapters.git.gitlab.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ObjectKindType {
    @SerialName("push")
    PUSH,
    @SerialName("tag_push")
    TAG_PUSH,
    @SerialName("issue")
    ISSUE,
    @SerialName("note")
    NOTE,
    @SerialName("merge_request")
    MERGE_REQUEST,
    @SerialName("wiki_page")
    WIKI_PAGE,
    @SerialName("pipeline")
    PIPELINE,
    @SerialName("build")
    BUILD,
    @SerialName("deployment")
    DEPLOYMENT,
    @SerialName("feature_flag")
    FEATURE_FLAG
}

@Serializable
data class ObjectKind(
    @SerialName("object_kind")
    val objectKind: ObjectKindType
)