package com.github.raink1208.radioBot.model

import kotlinx.serialization.Serializable

@Serializable
data class TwitterUser(val data: UserData) {
    @Serializable
    data class UserData(val id: String, val name: String, val username: String)
}
