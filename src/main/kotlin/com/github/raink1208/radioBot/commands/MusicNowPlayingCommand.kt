package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import com.github.raink1208.radioBot.util.infoEmbed
import net.dv8tion.jda.api.entities.Message

@CommandDescription("nowplaying", "再生中の曲の情報", ["np","nowp"])
object MusicNowPlayingCommand: ICommand {
    override fun execute(message: Message, args: String) {
        if (Main.instance.existsGuildAudioPlayer(message.guild)) {
            val musicManager = Main.instance.getGuildAudioPlayer(message.guild)
            val now = musicManager.player.playingTrack
            message.channel.sendMessageEmbeds(now.infoEmbed()).queue()
        }
    }
}
