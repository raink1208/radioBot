package com.github.raink1208.radiobot.service

import com.github.raink1208.radiobot.model.Playlist
import com.github.raink1208.radiobot.repository.PlaylistRepository
import net.dv8tion.jda.api.entities.User

object PlaylistService {
    private val playlistRepository = PlaylistRepository()

    fun createPlaylist(playlistName: String, user: User): Boolean {
        if (playlistRepository.existsPlaylist(playlistName))
            return false
        val playlist = Playlist(playlistName, user.idLong, mutableListOf())
        playlistRepository.save(playlist)
        return true
    }

    fun deletePlaylist(playlistName: String, user: User): Boolean {
        val playlist = playlistRepository.find(playlistName) ?: return false
        return if (playlist.author == user.idLong) {
            playlistRepository.delete(playlistName)
            true
        } else {
            false
        }
    }

    fun getEntirePlaylist(): List<Playlist> {
        return playlistRepository.getEntirePlaylist()
    }
}