package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.aandg.AandGRadioAudioTrack
import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager

@CommandDescription("radio", "超!A&Gのラジオを再生",[], 0)
object RadioPlayCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val musicManager = Main.instance.getGuildAudioPlayer(message.guild)
        val audioTrack = AandGRadioAudioTrack()

        val guild = message.guild
        val voiceChannel = message.member?.voiceState?.channel ?: return

        play(guild, voiceChannel, musicManager, audioTrack)
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
}