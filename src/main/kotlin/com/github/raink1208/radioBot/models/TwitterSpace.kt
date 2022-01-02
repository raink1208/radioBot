package com.github.raink1208.radioBot.models

import kotlinx.serialization.Serializable

@Serializable
data class TwitterSpace(val data: List<SpaceData>) {
    @Serializable
    data class SpaceData(val id: String, val state: String)
}