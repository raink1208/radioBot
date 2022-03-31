package com.github.raink1208.radiobot.model

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val name: String,
    val author: Long,
    val contents: List<PlaylistItem>
)
