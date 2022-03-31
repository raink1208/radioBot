package com.github.raink1208.radiobot

import com.github.raink1208.radiobot.audio.GuildMusicManager
import com.github.raink1208.radiobot.command.CommandHandler
import com.github.raink1208.radiobot.commands.*
import com.github.raink1208.radiobot.listener.CommandListener
import com.github.raink1208.radiobot.listener.EventListener
import com.github.raink1208.radiobot.listener.InteractionListener
import com.github.raink1208.radiobot.util.Config
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.internal.entities.EntityBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.security.auth.login.LoginException

fun main() {
    try {
        val token = Config.getDiscordToken()
        Main().start(token)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

class Main: ListenerAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(Main::class.java)
    companion object {
        lateinit var instance: Main
        private set
    }

    lateinit var jda: JDA

    private val commands = setOf(
        MusicPlayCommand, VCLeaveCommand, MusicLoopCommand, MusicNowPlayingCommand, MusicQueueCommand,
        MusicSkipCommand, RadioPlayCommand, QueueLoopCommand, SpacePlayCommand, MusicSearchCommand,
        PlaylistCommand
    )

    val playerManager = DefaultAudioPlayerManager()
    val musicManagers = mutableMapOf<Long, GuildMusicManager>()

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
    }

    fun start(token: String) {
        instance = this

        try {
            jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .addEventListeners(EventListener)
                .addEventListeners(CommandListener)
                .addEventListeners(InteractionListener)
                .setActivity(EntityBuilder.createActivity("音量注意", null, Activity.ActivityType.CUSTOM_STATUS))
                .build()

        } catch (e: LoginException) {
            e.printStackTrace()
        }
        registerCommands()
    }

    private fun registerCommands() {
        val commandsUpdate = jda.updateCommands()
        for (command in commands) {
            commandsUpdate.addCommands(command.commandData)
            CommandHandler.registerCommand(command)
        }
        commandsUpdate.queue()
    }

    fun existsGuildAudioPlayer(guild: Guild): Boolean {
        return musicManagers[guild.id.toLong()] != null
    }

    @Synchronized
    fun getGuildAudioPlayer(guild: Guild): GuildMusicManager {
        val guildId: Long = guild.id.toLong()
        var musicManager: GuildMusicManager? = musicManagers[guildId]
        if (musicManager == null) {
            musicManager = GuildMusicManager(playerManager)
            musicManagers[guildId] = musicManager
        }
        guild.audioManager.sendingHandler = musicManager.getSendHandler()
        return musicManager
    }

    @Synchronized
    fun removeGuildAudioPlayer(guild: Guild) {
        val guildId = guild.id.toLong()
        musicManagers.remove(guildId)
    }

    override fun onReady(event: ReadyEvent) {
        logger.info(jda.selfUser.name + " is Started!!")
    }
}