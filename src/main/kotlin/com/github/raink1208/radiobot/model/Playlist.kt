package com.github.raink1208.radiobot.model

data class Playlist(
    val name: String,
    val author: Long,
    val contents: List<PlaylistItem>
)
