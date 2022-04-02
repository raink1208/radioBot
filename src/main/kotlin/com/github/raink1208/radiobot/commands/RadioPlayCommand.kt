package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.audio.AudioPlayer
import com.github.raink1208.radiobot.audiosource.aandg.AandGRadioAudioTrack
import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

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
        AudioPlayer().play(guild, audioChannel, musicManager, audioTrack)
    }
}