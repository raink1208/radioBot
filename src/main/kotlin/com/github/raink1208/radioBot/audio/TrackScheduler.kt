package com.github.raink1208.radioBot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue


class TrackScheduler(private val player: AudioPlayer, private val musicManager: GuildMusicManager): AudioEventAdapter() {
    private var queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()

    fun queue(track: AudioTrack) {
        if (!player.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    fun getMusicList(): List<AudioTrack> {
        return queue.toList()
    }

    private fun nextTrack() {
        player.startTrack(queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            if (musicManager.musicLoop) {
                player.startTrack(track.makeClone(), false)
                return
            } else if (musicManager.queueLoop) {
                queue.offer(track.makeClone())
            }
            nextTrack()
        }
    }
}