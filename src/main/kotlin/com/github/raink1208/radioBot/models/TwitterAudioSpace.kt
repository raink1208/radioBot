package com.github.raink1208.radioBot.models

import kotlinx.serialization.Serializable

@Serializable
data class TwitterAudioSpace(val data: AudioSpace) {
    @Serializable
    data class AudioSpace(val audioSpace: AudioSpaceData)

    @Serializable
    data class AudioSpaceData(val metadata: MetaData)

    @Serializable
    data class MetaData(val media_key: String, val title: String)
}