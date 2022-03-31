package com.github.raink1208.radiobot.audio

import com.github.raink1208.radiobot.Main
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.managers.AudioManager

object AudioPlayer {
    fun loadAndPlay(channel: MessageChannel, guild: Guild, audioChannel: AudioChannel, trackUrl: String) {
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        Main.instance.playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                channel.sendMessage("キューに曲を追加しました: " + track.info.title).queue()
                play(guild, audioChannel, musicManager, track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                for (audioTrack in playlist.tracks) {
                    play(guild, audioChannel, musicManager, audioTrack)
                }
                channel.sendMessage("キューにプレイリストを追加しました:" + playlist.name).queue()
            }

            override fun noMatches() {
                channel.sendMessage("動画が見つかりませんでした: $trackUrl").queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                channel.sendMessage("読み込みできませんでした").queue()
            }
        })
    }

    private fun play(guild: Guild, audioChannel: AudioChannel, musicManager: GuildMusicManager, track: AudioTrack) {
        connectVoiceChannel(guild.audioManager, audioChannel)
        musicManager.scheduler.queue(track)
    }

    private fun connectVoiceChannel(audioManager: AudioManager, audioChannel: AudioChannel) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(audioChannel)
        }
    }
}