package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.command.CommandBase
import com.github.raink1208.radiobot.util.infoEmbed
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

class MusicNowPlayingCommand: CommandBase {
    override val commandData = Commands.slash("nowplaying", "再生中の音楽の情報を取得")
    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使用できません").queue()
            return
        }
        val musicManager = Main.instance.musicManagers[guild.idLong]
        if (musicManager == null) {
            command.reply("Botは使われていません").queue()
            return
        }
        if (Main.instance.existsGuildAudioPlayer(guild)) {
            val now = musicManager.player.playingTrack
            command.replyEmbeds(now.infoEmbed()).queue()
        }
    }
}