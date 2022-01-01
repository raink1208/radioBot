package com.github.raink1208.radioBot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason


class GuildMusicManager(manager: AudioPlayerManager) {
    var player: AudioPlayer = manager.createPlayer()
    var scheduler: TrackScheduler = TrackScheduler(player, this)
    var musicLoop = false
    var queueLoop = false

    init {
        player.addListener(scheduler)
    }

    fun toggleMusicLoop() {
        musicLoop = !musicLoop
    }

    fun toggleQueueLoop() {
        queueLoop = !queueLoop
    }

    fun skipTrack() {
        scheduler.onTrackEnd(player, player.playingTrack, AudioTrackEndReason.FINISHED)
    }

    fun getSendHandler(): AudioPlayerSendHandler {
        return AudioPlayerSendHandler(player)
    }
}