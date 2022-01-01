package com.github.raink1208.radioBot.aandg

import com.sedmelluq.discord.lavaplayer.source.stream.M3uStreamSegmentUrlProvider
import com.sedmelluq.discord.lavaplayer.source.stream.MpegTsM3uStreamAudioTrack
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import org.apache.commons.codec.net.URLCodec
import org.apache.http.client.methods.HttpGet
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class AandGRadioAudioTrack: MpegTsM3uStreamAudioTrack(trackInfo) {
    companion object {
        val trackInfo = AudioTrackInfo("超! A&G", "文化放送", Units.DURATION_MS_UNKNOWN, AudioReference("", "").identifier, true,
            AandGReference.playlistUrl
        )
    }
    private val Logger = LoggerFactory.getLogger(AandGRadioAudioTrack::class.java)

    private val segmentUrlProvider = AandGRadioSegmentUrlProvider()
    private val httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager()

    override fun getSegmentUrlProvider(): M3uStreamSegmentUrlProvider {
        return segmentUrlProvider
    }

    override fun getHttpInterface(): HttpInterface {
        return httpInterfaceManager.`interface`
    }

    private fun loadRadioInfoList(): List<String> {
        val lineText = DataFormatTools.streamToLines(httpInterface.execute(HttpGet(AandGReference.radioInfoUrl)).entity.content, StandardCharsets.UTF_8)
        val codec = URLCodec("UTF-8")
        val regex = """'(.*?)'""".toRegex()

        val result = mutableListOf<String>()

        for (line in lineText) {
            val decoded = codec.decode(line)
            result.add(regex.find(decoded)?.groupValues?.getOrNull(1) ?: "")
        }

        return result
    }
}