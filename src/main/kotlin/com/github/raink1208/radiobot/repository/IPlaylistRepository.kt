package com.github.raink1208.radiobot.repository

import com.github.raink1208.radiobot.model.Playlist
import java.util.UUID

interface IPlaylistRepository {
    fun save(playlist: Playlist)
    fun delete(uuid: UUID)
    fun getEntirePlaylist(): List<Playlist>
    fun existsPlaylist(playlistName: String): Boolean
    fun find(playlistName: String): Playlist?
}