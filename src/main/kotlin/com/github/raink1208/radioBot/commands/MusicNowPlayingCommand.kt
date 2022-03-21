package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandBase
import com.github.raink1208.radioBot.util.infoEmbed
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object MusicNowPlayingCommand: CommandBase {
    override val commandData = Commands.slash("nowplaying", "再生中の音楽の情報を取得")
    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使用できません").queue()
            return
        }
        if (Main.instance.existsGuildAudioPlayer(guild)) {
            val musicManager = Main.instance.getGuildAudioPlayer(guild)
            val now = musicManager.player.playingTrack
            command.replyEmbeds(now.infoEmbed()).queue()
        }
    }
}