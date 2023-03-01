package com.tikhon.app.events.dto.git

data class GitProject(
    val name: String,
    val namespace: String,
    val pathWithNameSpace: String
) {
    companion object {
        private val regex = Regex("https?://(.+?)/(.+)/?")
        fun fromURL(url: String): GitProject {
            val match = regex.find(url) ?: throw Exception("Error parse git source URL: $url")
            if (match.groupValues.size != 3) {
                throw Exception("Error parse git project URL, expected 3 groups, but found: ${match.groupValues.joinToString()}")
            }
            val pathWithNameSpace =  match.groupValues.last()
            val splinted = pathWithNameSpace.split("/").filterNot { it.isEmpty() }
            if (splinted.size < 2) {
                throw Exception("Error parse git project URL, expected namespace and project, but found: ${splinted.joinToString()}")
            }

            return GitProject(
                name = splinted.last(),
                namespace = splinted.subList(0, splinted.lastIndex).joinToString("/"),
                pathWithNameSpace = splinted.joinToString("/")
            )
        }
    }
}
