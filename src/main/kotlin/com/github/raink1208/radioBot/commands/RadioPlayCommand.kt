package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.aandg.AandGRadioAudioTrack
import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandBase
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.managers.AudioManager

object RadioPlayCommand: CommandBase {
    override val commandData = Commands.slash("radio", "超!A&Gラジオを再生する")

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使えません").queue()
            return
        }
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        val audioTrack = AandGRadioAudioTrack()
        val audioChannel = command.member?.voiceState?.channel
        if (audioChannel == null) {
            command.reply("VCに参加してから使用してください").queue()
            return
        }
        play(guild, audioChannel, musicManager, audioTrack)
    }

    private fun play(guild: Guild, audioChannel: AudioChannel, musicManager: GuildMusicManager, track: AudioTrack) {
        connectVoiceChannel(guild.audioManager, audioChannel)
        musicManager.scheduler.queue(track)
    }

    private fun connectVoiceChannel(audioManager: AudioManager, audioChannel: AudioChannel) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(audioChannel)
        }
    }
}