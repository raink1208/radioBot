package com.github.raink1208.radiobot.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistItem(
    val title: String,
    val url: String
)