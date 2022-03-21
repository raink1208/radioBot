package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object MusicSkipCommand: CommandBase {
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

/*object MusicSkipCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val channel = message.channel
        if (channel is TextChannel) skipTrack(channel)
    }

    private fun skipTrack(channel: TextChannel) {
        val musicManager = Main.instance.getGuildAudioPlayer(channel.guild)
        musicManager.skipTrack()
        channel.sendMessage("曲をスキップします").queue()
    }
}*/