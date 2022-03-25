package com.github.raink1208.radioBot.audiosource.aandg

import com.sedmelluq.discord.lavaplayer.container.playlists.ExtendedM3uParser
import com.sedmelluq.discord.lavaplayer.source.stream.M3uStreamSegmentUrlProvider
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest

class AandGRadioSegmentUrlProvider: M3uStreamSegmentUrlProvider() {
    override fun getQualityFromM3uDirective(directiveLine: ExtendedM3uParser.Line?): String {
        return "default"
    }

    override fun fetchSegmentPlaylistUrl(httpInterface: HttpInterface?): String {
        return AandGReference.playlistUrl
    }

    override fun createSegmentGetRequest(url: String): HttpUriRequest {
        return HttpGet(url)
    }
}