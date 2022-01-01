package com.github.raink1208.radioBot.eventListener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object URLMessageListener: ListenerAdapter() {
    private const val baseURL = "http://is.gd/create.php?format=simple&format=json&url="
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        var message = event.message.contentRaw
        if (!message.startsWith("url. ")) return
        event.message.delete().queue()
        val urls = extractURLs(message)
        for (url in urls) {
            val req = HttpRequest.newBuilder(URI(baseURL + url))
            val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build().send(req.build(), HttpResponse.BodyHandlers.ofString())
            val shortenedURL = getShortenedURL(client.body())
            message = message.replace(url, shortenedURL)
        }
        message = message.replaceFirst("url.", event.author.name)
        event.channel.sendMessage(message).queue()
    }

    fun extractURLs(text: String): List<String> {
        val regex = Regex("(http://|https://)[\\w.\\-/:#?=&;%~+]+")
        val urls = regex.findAll(text).map { it.value }
        return urls.toList()
    }

    fun getShortenedURL(body: String): String {
        val regex = Regex("https://is.gd/(.*?)(?=\")")
        return regex.find(body)?.value ?: ""
    }
}