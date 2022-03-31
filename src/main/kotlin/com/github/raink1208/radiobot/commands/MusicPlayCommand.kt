package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.audio.AudioPlayer
import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object MusicPlayCommand: CommandBase {
    override val commandData = Commands.slash("play", "音楽を再生する")
        .addOption(OptionType.STRING, "url", "再生する音楽のURL", true)

    override fun execute(command: SlashCommandInteraction) {
        val channel = command.channel
        val guild = command.guild
        val audioChannel = command.member?.voiceState?.channel

        if (guild == null) {
            command.reply("Guild外では使用できません").queue()
            return
        }

        if (audioChannel == null) {
            command.reply("VC参加してから使用してください").queue()
            return
        }
        val audioManager = guild.audioManager

        if (audioManager.isConnected) {
            if (audioManager.connectedChannel?.id != audioChannel.id) {
                command.reply("既にほかのチャンネルで使われています").queue()
                return
            }
        }

        val url = command.getOption("url")?.asString
        if (url == null) {
            command.reply("urlが入力されていません").queue()
            return
        }
        command.reply("再生の準備をしています").queue()
        AudioPlayer.loadAndPlay(channel, guild, audioChannel, url)
    }
}