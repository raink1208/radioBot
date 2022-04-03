package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

class MusicSkipCommand: CommandBase {
    override val commandData = Commands.slash("skip", "音楽をスキップする")

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使えません").queue()
            return
        }
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        musicManager.skipTrack()
        command.reply("実行しています").queue()
        command.channel.sendMessage("音楽をスキップします").queue()
    }
}