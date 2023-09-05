package com.tikhon.app.events.dto.git

data class GitMergeRequest(
    val title: String,
    val description: String,
    val url: String,
    val action: GitMergeRequestAction,
    val sourceBranch: String,
    val targetBranch: String,
    val author: GitUser,
    val reviewers: List<GitUser>
)

enum class GitMergeRequestAction {
    OPEN,
    CLOSE,
    REOPEN,
    UPDATE,
    APPROVED,
    UNAPPROVED,
    APPROVAL,
    UNAPPROVAL,
    MERGE;

    companion object {

        fun fromString(value: String): GitMergeRequestAction {
            return when(value.lowercase()) {
                "open" -> OPEN
                "close" -> CLOSE
                "reopen" -> REOPEN
                "update" -> UPDATE
                "approved" -> APPROVED
                "unapproved" -> UNAPPROVED
                "approval" -> APPROVAL
                "unapproval" -> UNAPPROVAL
                "merge" -> MERGE
                else -> throw IllegalStateException("Unknown merge request action '$value'")
            }
        }
    }
}