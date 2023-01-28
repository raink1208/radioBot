package com.github.raink1208.radiobot.model

import java.util.UUID

data class Playlist(
    val uuid: UUID,
    val name: String,
    val author: Long,
    var isPublic: Boolean = false,
    var upstream: String = "",
    val guildId: Long,
    val contents: MutableList<PlaylistItem>
)
