package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import com.github.raink1208.radioBot.space.SpaceAudioTrack
import com.github.raink1208.radioBot.space.SpaceReference
import com.github.raink1208.radioBot.util.Config
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager
import org.apache.http.HttpEntity
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.net.URI
import java.nio.charset.Charset

@Serializable
data class CreatorIDs(val data: List<SpaceModel>, val meta: Meta)

@Serializable
data class Meta(val result_count: Int)

@Serializable
data class SpaceModel(val id: String, val state: String)

@Serializable
data class GraphQLResponse(val data: SpaceData)

@Serializable
data class SpaceData(val audioSpace: AudioSpace)

@Serializable
data class AudioSpace(val metadata: MetaData)

@Serializable
data class MetaData(val media_key: String)

@Serializable
data class StreamStatus(val source: StreamSource)

@Serializable
data class StreamSource(val location: String)

@Serializable
data class User(val data: UserData)

@Serializable
data class UserData(val id: String, val name: String, val username: String)

private val json = Json { ignoreUnknownKeys = true }

@CommandDescription("space", "", [], 1)
object SpacePlayCommand: ICommand {
    val apiToken = Config.getTwitterToken()

    val userToken = Config.getUserToken()
    val csrfToken = Config.getCSRFToken()
    val cookie = Config.getTwitterCookie()

    val httpClient = HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setCookieSpec("standard").build())
        .build()


    override fun execute(message: Message, args: String) {
        val musicManager = Main.instance.getGuildAudioPlayer(message.guild)
        val userID = json.decodeFromString<User>(getUserID(args))
        val audioTrack = createAudioTrack(userID.data.id)

        val guild = message.guild
        val voiceChannel = message.member?.voiceState?.channel ?: return

        play(guild, voiceChannel, musicManager, audioTrack)
    }

    private fun getUserID(twitterName: String): String {
        val uriBuilder = URIBuilder("https://api.twitter.com/2/users/by/username/$twitterName")
        val entity = apiAuthentication(uriBuilder.build())

        return EntityUtils.toString(entity, Charset.defaultCharset())
    }

    private fun createAudioTrack(userID: String): AudioTrack {
        val creatorIDs = json.decodeFromString<CreatorIDs>(checkSpace(userID))
        val graphQLResponse = json.decodeFromString<GraphQLResponse>(getMediaKey(creatorIDs.data[0].id))
        val streamStatus = json.decodeFromString<StreamStatus>(getAudioSpaceSourceLocation(graphQLResponse.data.audioSpace.metadata.media_key))
        return SpaceAudioTrack(AudioTrackInfo("space", "twitter space", Units.DURATION_MS_UNKNOWN, "", true, streamStatus.source.location))
    }

    private fun play(guild: Guild, voiceChannel: VoiceChannel, musicManager: GuildMusicManager, track: AudioTrack) {
        connectVoiceChannel(guild.audioManager, voiceChannel)
        musicManager.scheduler.queue(track)
    }

    private fun connectVoiceChannel(audioManager: AudioManager, voiceChannel: VoiceChannel) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
    }

    private fun checkSpace(userID: String): String {
        val uriBuilder = URIBuilder("https://api.twitter.com/2/spaces/by/creator_ids")
        val queryParameters = mutableListOf<BasicNameValuePair>()
        queryParameters.add(BasicNameValuePair("user_ids", userID))
        uriBuilder.addParameters(queryParameters.toList())
        val entity = apiAuthentication(uriBuilder.build())

        return EntityUtils.toString(entity, Charset.defaultCharset())
    }

    private fun getMediaKey(spaceId: String): String {
        val uriBuilder = URIBuilder(SpaceReference.graphQLURL)
        val queryParameters = mutableListOf<BasicNameValuePair>()
        queryParameters.add(BasicNameValuePair("variables","{\"id\":\"${spaceId}\",\"isMetatagsQuery\":false,\"withSuperFollowsUserFields\":true,\"withBirdwatchPivots\":false,\"withDownvotePerspective\":false,\"withReactionsMetadata\":false,\"withReactionsPerspective\":false,\"withSuperFollowsTweetFields\":true,\"withReplays\":true,\"withScheduledSpaces\":true}"))
        uriBuilder.addParameters(queryParameters.toList())
        val entity = userAuthentication(uriBuilder.build())

        return EntityUtils.toString(entity, Charset.defaultCharset())
    }

    private fun getAudioSpaceSourceLocation(mediaKey: String): String {
        val uriBuilder = URIBuilder(SpaceReference.liveVideoStreamStatusURL + mediaKey)
        val queryParameters = mutableListOf<BasicNameValuePair>()
        queryParameters.add(BasicNameValuePair("client", "web"))
        queryParameters.add(BasicNameValuePair("use_syndication_guest_id", "false"))
        queryParameters.add(BasicNameValuePair("cookie_set_host", "twitter.com"))
        uriBuilder.addParameters(queryParameters.toList())
        val entity = userAuthentication(uriBuilder.build())

        return EntityUtils.toString(entity, Charset.defaultCharset())
    }

    private fun apiAuthentication(uri: URI): HttpEntity {
        val httpGet = HttpGet(uri)
        httpGet.setHeader("authorization", String.format("Bearer %s", apiToken))
        httpGet.setHeader("content-type", "application/json")
        val response = httpClient.execute(httpGet)
        return response.entity
    }


    private fun userAuthentication(uri: URI): HttpEntity {
        val httpGet = HttpGet(uri)
        httpGet.setHeader("x-csrf-token", csrfToken)
        httpGet.setHeader("authorization", String.format("Bearer %s", userToken))
        httpGet.setHeader("content-type", "application/json")
        httpGet.setHeader("cookie", cookie)
        val response = httpClient.execute(httpGet)
        return response.entity
    }
}