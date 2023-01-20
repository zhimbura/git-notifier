package com.tikhon.app.events.dto.git

data class GitSource(
    val name: String,
    val url: String
) {
    companion object {
        private val regex = Regex("https?://(.+?)/")
        fun fromURL(url: String): GitSource {
            val match = regex.find(url) ?: throw Exception("Error parse git source URL: $url")
            val name =  match.groupValues.takeIf { it.size == 2 }?.last() ?: throw Exception("Error parse git source URL, expected 2 groups, but found: ${match.groupValues.joinToString()}")
            return GitSource(name, url)
        }
    }
}


