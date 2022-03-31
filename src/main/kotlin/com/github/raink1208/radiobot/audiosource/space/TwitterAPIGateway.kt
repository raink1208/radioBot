package com.github.raink1208.radiobot.audiosource.space

import com.github.raink1208.radiobot.model.TwitterAudioSpace
import com.github.raink1208.radiobot.model.TwitterSpace
import com.github.raink1208.radiobot.model.TwitterSpaceStreamStatus
import com.github.raink1208.radiobot.model.TwitterUser
import com.github.raink1208.radiobot.util.Config
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.HttpEntity
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

object TwitterAPIGateway {
    private val httpClient = HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setCookieSpec("standard").build())
        .build()

    private val apiToken = Config.getTwitterToken()
    private val userToken = Config.getUserToken()
    private val csrfToken = Config.getCSRFToken()
    private val cookie = Config.getTwitterCookie()

    private val json = Json { ignoreUnknownKeys = true }

    fun getUserByName(name: String): TwitterUser {
        val username = name.removePrefix("@")
        val uriBuilder = URIBuilder("https://api.twitter.com/2/users/by/username/$username")
        val httpGet = HttpGet(uriBuilder.build())
        val entity = apiAuthentication(httpGet)

        return json.decodeFromString(EntityUtils.toString(entity))
    }

    fun getSpaceByUserID(userID: String): TwitterSpace {
        val uriBuilder = URIBuilder("https://api.twitter.com/2/spaces/by/creator_ids")
        val queryParameters = mutableListOf<BasicNameValuePair>()
        queryParameters.add(BasicNameValuePair("user_ids", userID))
        uriBuilder.addParameters(queryParameters.toList())
        val httpGet = HttpGet(uriBuilder.build())
        val entity = apiAuthentication(httpGet)

        return json.decodeFromString(EntityUtils.toString(entity))
    }

    fun getAudioSpaceBySpaceID(spaceID: String): TwitterAudioSpace {
        val uriBuilder = URIBuilder(SpaceReference.graphQLURL)
        val queryParameters = mutableListOf<BasicNameValuePair>()
        queryParameters.add(BasicNameValuePair("variables","{\"id\":\"${spaceID}\",\"isMetatagsQuery\":false,\"withSuperFollowsUserFields\":true,\"withBirdwatchPivots\":false,\"withDownvotePerspective\":false,\"withReactionsMetadata\":false,\"withReactionsPerspective\":false,\"withSuperFollowsTweetFields\":true,\"withReplays\":true,\"withScheduledSpaces\":true}"))
        uriBuilder.addParameters(queryParameters.toList())
        val httpGet = HttpGet(uriBuilder.build())
        val entity = userAuthentication(httpGet)

        return json.decodeFromString(EntityUtils.toString(entity))
    }

    fun getSpaceStreamStatus(mediaKey: String): TwitterSpaceStreamStatus {
        val uriBuilder = URIBuilder(SpaceReference.liveVideoStreamStatusURL + mediaKey)
        val queryParameters = mutableListOf<BasicNameValuePair>()
        queryParameters.add(BasicNameValuePair("client", "web"))
        queryParameters.add(BasicNameValuePair("use_syndication_guest_id", "false"))
        queryParameters.add(BasicNameValuePair("cookie_set_host", "twitter.com"))
        uriBuilder.addParameters(queryParameters.toList())
        val httpGet = HttpGet(uriBuilder.build())
        val entity = userAuthentication(httpGet)

        return json.decodeFromString(EntityUtils.toString(entity))
    }

    private fun apiAuthentication(httpRequest: HttpRequestBase): HttpEntity {
        httpRequest.setHeader("authorization", String.format("Bearer %s", apiToken))
        httpRequest.setHeader("content-type", "application/json")
        val response = httpClient.execute(httpRequest)
        return response.entity
    }

    private fun userAuthentication(httpRequest: HttpRequestBase): HttpEntity {
        httpRequest.setHeader("x-csrf-token", csrfToken)
        httpRequest.setHeader("authorization", String.format("Bearer %s", userToken))
        httpRequest.setHeader("content-type", "application/json")
        httpRequest.setHeader("cookie", cookie)
        val response = httpClient.execute(httpRequest)
        return response.entity
    }
}