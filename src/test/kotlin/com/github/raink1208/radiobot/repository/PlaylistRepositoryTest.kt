package com.github.raink1208.radiobot.repository

import com.github.raink1208.radiobot.model.Playlist
import com.github.raink1208.radiobot.model.PlaylistItem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PlaylistRepositoryTest {
    @Test
    fun savePlaylistTest() {
        val repository = PlaylistRepository()
        val contents = mutableListOf(PlaylistItem("test", "test"))
        val playlist = Playlist("test", 43274832, true, 9217489235467324,contents)
        repository.save(playlist)
        playlist.isPublic = false
        repository.save(playlist)

        println(repository.find("test"))
    }
}