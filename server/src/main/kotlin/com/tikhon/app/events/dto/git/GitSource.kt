package com.tikhon.app.events.dto.git

data class GitSource(
    val name: String,
    val url: String
) {
    companion object {
        private val regex = Regex("https?://(.+?)/")
        fun fromURL(url: String): GitSource {
            val match = regex.find(url) ?: throw Exception("Error parse git source URL: $url")
            if (match.groupValues.size != 2) {
                throw Exception("Error parse git source URL, expected 2 groups, but found: ${match.groupValues.joinToString()}")
            }
            val (gitUrl, name) =  match.groupValues
            return GitSource(name, gitUrl)
        }
    }
}


