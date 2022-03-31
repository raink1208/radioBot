package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object MusicQueueCommand: CommandBase {
    override val commandData = Commands.slash("queue", "再生リストを表示する")

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使えません").queue()
            return
        }
        val musicManager = Main.instance.musicManagers[guild.idLong]
        if (musicManager == null) {
            command.reply("Botは使われていません").queue()
            return
        }
        val audioTrackList = musicManager.scheduler.getMusicList()
        var text = "再生リスト\n"
        for (audioTrack in audioTrackList) {
            text += audioTrack.info.title + " : " + audioTrack.info.length + "\n"
        }
        command.reply(text).queue()
    }
}