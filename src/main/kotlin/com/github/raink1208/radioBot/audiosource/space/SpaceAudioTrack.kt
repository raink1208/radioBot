package com.github.raink1208.radioBot.audiosource.space

import com.sedmelluq.discord.lavaplayer.container.adts.AdtsAudioTrack
import com.sedmelluq.discord.lavaplayer.source.stream.M3uStreamAudioTrack
import com.sedmelluq.discord.lavaplayer.source.stream.M3uStreamSegmentUrlProvider
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor
import java.io.InputStream

class SpaceAudioTrack(trackInfo: AudioTrackInfo): M3uStreamAudioTrack(trackInfo) {
    private val segmentUrlProvider = SpaceSegmentUrlProvider(trackInfo)
    private val httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager()

    override fun getSegmentUrlProvider(): M3uStreamSegmentUrlProvider {
        return segmentUrlProvider
    }

    override fun getHttpInterface(): HttpInterface {
        return httpInterfaceManager.`interface`
    }

    override fun processJoinedStream(localExecutor: LocalAudioTrackExecutor?, stream: InputStream?) {
        processDelegate(AdtsAudioTrack(trackInfo, stream), localExecutor)
    }
}