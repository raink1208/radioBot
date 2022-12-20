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
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User

object PlaylistService {
    private val playlistRepository = PlaylistRepository()
    private val playerManager = DefaultAudioPlayerManager()

    init {
        playerManager.registerSourceManager(YoutubeAudioSourceManager(false))
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

    fun loadPlaylist(playlistName: String, user: User, guild: Guild, url: String): CreatePlaylist {
        if (!checkPlaylistName(playlistName)) {
            return CreatePlaylist.NAME_ERROR
        }
        if (playlistRepository.existsPlaylist(playlistName)) {
            return CreatePlaylist.NAME_EXISTS
        }
        val list = mutableListOf<PlaylistItem>()
        playerManager.loadItem(url, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                list.add(PlaylistItem(track.info.title, track.info.uri))
                val pl = Playlist(playlistName, user.idLong, false, url, guild.idLong, list)
                playlistRepository.save(pl)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                for (audioTrack in playlist.tracks) {
                    list.add(PlaylistItem(audioTrack.info.title, audioTrack.info.uri))
                }
                val pl = Playlist(playlistName, user.idLong, false, url, guild.idLong, list)
                playlistRepository.save(pl)
            }

            override fun noMatches() {
            }

            override fun loadFailed(exception: FriendlyException) {
            }
        })
        return CreatePlaylist.SUCCESS
    }

    fun checkPlaylistName(playlistName: String): Boolean {
        val regex = Regex("[\\x00-\\x1f<>:\"/\\\\|?*]|^(CON|PRN|AUX|NUL|COM[0-9]|LPT[0-9]|CLOCK\\$)(\\.|\$)|[\\\\. ]\$")
        if (regex.matches(playlistName)) {
            return false
        }
        return true
    }

    fun getEntirePlaylist(): List<Playlist> {
        return playlistRepository.getEntirePlaylist()
    }

    fun getPlaylistFindByGuild(guild: Guild): List<Playlist> {
        val list = getEntirePlaylist()
        val fixedList = mutableListOf<Playlist>()
        for (i in list) {
            if (i.isPublic) {
                fixedList.add(i)
                continue
            }
            if (i.guildId == guild.idLong) {
                fixedList.add(i)
            }
        }
        return fixedList
    }

    fun getPlaylistFindByUser(user: User): List<Playlist> {
        val list = getEntirePlaylist()
        val fixedList = mutableListOf<Playlist>()
        for (i in list) {
            if (i.author == user.idLong)
                fixedList.add(i)
        }
        return fixedList
    }

    enum class CreatePlaylist {
        SUCCESS,
        NAME_EXISTS,
        NAME_ERROR
    }
}