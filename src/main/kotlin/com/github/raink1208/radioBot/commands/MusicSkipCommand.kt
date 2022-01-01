package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

@CommandDescription("skip", "曲をスキップします", ["sk"])
object MusicSkipCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val channel = message.channel
        if (channel is TextChannel) skipTrack(channel)
    }

    private fun skipTrack(channel: TextChannel) {
        val musicManager = Main.instance.getGuildAudioPlayer(channel.guild)
        musicManager.skipTrack()
        channel.sendMessage("曲をスキップします").queue()
    }
}