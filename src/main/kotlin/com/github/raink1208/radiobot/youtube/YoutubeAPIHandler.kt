package com.github.raink1208.radiobot.youtube

import com.github.raink1208.radiobot.util.Config
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube

class YoutubeAPIHandler {
    private val httpTransport = NetHttpTransport()
    private val jsonFactory: GsonFactory = GsonFactory.getDefaultInstance()
    private val youtube: YouTube = YouTube.Builder(httpTransport, jsonFactory) {
    }.setApplicationName("youtube-cmdline-search").build()

    fun search(keyword: String): List<YouTubeSearchResult> {
        val search = youtube.search().list(listOf("id", "snippet"))
        search.key = Config.getYoutubeAPIKey()
        search.q = keyword
        search.type = listOf("video")
        search.maxResults = 5
        search.fields = "items(id/videoId,snippet/title)"

        val result = search.execute()

        val resultVideo = mutableListOf<YouTubeSearchResult>()

        for (item in result.items) {
            resultVideo.add(YouTubeSearchResult(item.snippet.title, item.id.videoId))
        }
        return resultVideo
    }
}