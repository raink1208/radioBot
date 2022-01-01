package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import net.dv8tion.jda.api.entities.Message

@CommandDescription("queue", "再生リストを表示します", ["q", "list"])
object MusicQueueCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val guildId = message.guild.idLong
        val musicManager = Main.instance.musicManagers[guildId]

        if (musicManager == null) {
            message.channel.sendMessage("Botは使われていません").queue()
            return
        }

        val audioTrackList = musicManager.scheduler.getMusicList()
        var text = "再生リスト\n"

        for (audioTrack in audioTrackList) {
            text += audioTrack.info.title + " : " + audioTrack.info.length + "\n"
        }
        message.channel.sendMessage(text).queue()
    }
}