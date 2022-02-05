package com.github.raink1208.radioBot

import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandHandler
import com.github.raink1208.radioBot.commands.*
import com.github.raink1208.radioBot.eventListener.EventListener
import com.github.raink1208.radioBot.util.Config
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.internal.entities.EntityBuilder
import org.slf4j.LoggerFactory
import javax.security.auth.login.LoginException

val logger = LoggerFactory.getLogger(Main::class.java)

fun main() {
    try {
        val token = Config.getDiscordToken()
        Main().start(token)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

class Main: ListenerAdapter() {
    companion object {
        lateinit var instance: Main
        private set

        const val COMMAND_PREFIX = "r!"
    }

    lateinit var jda: JDA

    private val commands = setOf(
        MusicLoopCommand, MusicNowPlayingCommand, MusicPlayCommand, MusicQueueCommand, MusicSkipCommand, RadioPlayCommand,
        MusicSearchCommand, QueueLoopCommand, SpacePlayCommand, VCLeaveCommand
    )

    val playerManager = DefaultAudioPlayerManager()
    val musicManagers = mutableMapOf<Long, GuildMusicManager>()

    val commandHandler = CommandHandler()

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
                .setActivity(EntityBuilder.createActivity("音量注意", null, Activity.ActivityType.DEFAULT))
                .build()

            commandHandler.registerCommands(commands)
        } catch (e: LoginException) {
            e.printStackTrace()
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val command = event.message.contentRaw.split(" ")

        if (command[0].isEmpty()) return
        if (!command[0].startsWith(COMMAND_PREFIX)) return

        val cmd = command[0].drop(COMMAND_PREFIX.length)
        val args = command.drop(1).joinToString(" ")

        commandHandler.findAndExecute(cmd, event.message, args)
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