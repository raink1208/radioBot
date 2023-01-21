package com.github.raink1208.radiobot.repository

import com.github.raink1208.radiobot.model.Playlist

interface IPlaylistRepository {
    fun save(playlist: Playlist)
    fun delete(playlistName: String)
    fun getEntirePlaylist(): List<Playlist>
    fun existsPlaylist(playlistName: String): Boolean
    fun find(playlistName: String): Playlist?
}