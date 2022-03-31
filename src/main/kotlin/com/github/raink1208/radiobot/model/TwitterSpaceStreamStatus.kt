package com.github.raink1208.radiobot.model

import kotlinx.serialization.Serializable

@Serializable
data class TwitterSpaceStreamStatus(val source:StreamSource) {
    @Serializable
    data class StreamSource(val location: String)
}
