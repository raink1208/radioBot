package com.github.raink1208.radiobot.service

import com.github.raink1208.radiobot.model.Playlist
import com.github.raink1208.radiobot.model.PlaylistItem
import com.github.raink1208.radiobot.repository.PlaylistRepository
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.User

object PlaylistService {
    private val playlistRepository = PlaylistRepository()
    private val playerManager = DefaultAudioPlayerManager()

    init {
        playerManager.registerSourceManager(YoutubeAudioSourceManager(false))
    }

    fun createPlaylist(playlistName: String, user: User): Boolean {
        if (playlistRepository.existsPlaylist(playlistName))
            return false
        val playlist = Playlist(playlistName, user.idLong, mutableListOf())
        playlistRepository.save(playlist)
        return true
    }

    fun getPlaylist(playlistName: String): Playlist? {
        return playlistRepository.find(playlistName)
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

    fun loadPlaylist(playlistName: String, user: User, url: String) {
        val list = mutableListOf<PlaylistItem>()
        playerManager.loadItem(url, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                list.add(PlaylistItem(track.info.title, track.info.uri))
                val pl = Playlist(playlistName, user.idLong, list)
                playlistRepository.save(pl)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                for (audioTrack in playlist.tracks) {
                    list.add(PlaylistItem(audioTrack.info.title, audioTrack.info.uri))
                }
                val pl = Playlist(playlistName, user.idLong, list)
                playlistRepository.save(pl)
            }

            override fun noMatches() {
            }

            override fun loadFailed(exception: FriendlyException) {
            }
        })
    }

    fun getEntirePlaylist(): List<Playlist> {
        return playlistRepository.getEntirePlaylist()
    }
}