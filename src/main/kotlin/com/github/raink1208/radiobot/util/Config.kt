package com.github.raink1208.radiobot.util

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.lang.NullPointerException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object Config {
    private val file: File = Paths.get("./setting.yml").toAbsolutePath().normalize().toFile()
    private val config: Map<String, *>

    init {
        if (!file.exists()) {
            generateSettingFile()
        }

        config = Yaml().load(file.reader())
    }

    private fun generateSettingFile() {
        val source = javaClass.classLoader.getResource("setting.yml")
            ?: throw Exception("cannot get setting.yml")
        Files.copy(Paths.get(source.toURI()), Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING)
    }

    fun getDiscordToken(): String = getString("discordToken")

    fun getYoutubeAPIKey(): String = getString("youtubeAPIKey")

    fun getTwitterToken(): String = getString("twitterToken")

    fun getUserToken(): String = getString("userToken")

    fun getCSRFToken(): String = getString("csrfToken")

    fun getTwitterCookie(): String = getString("twitterCookie")

    fun get(path: String): Any {
        return config[path] ?: throw NullPointerException("setting.yml:" + path + "は存在しません")
    }

    private fun getString(path: String): String {
        return get(path) as String
    }

    private fun getInt(path: String): Int {
        return get(path) as Int
    }

    private fun getDouble(path: String): Double {
        return get(path) as Double
    }
}