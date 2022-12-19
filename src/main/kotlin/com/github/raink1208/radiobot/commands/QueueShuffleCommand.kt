package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

class QueueShuffleCommand: CommandBase {
    override val commandData = Commands.slash("shuffle", "再生リストをシャッフルする")

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使えません").queue()
            return
        }
        if (!Main.instance.existsGuildAudioPlayer(guild)) {
            command.reply("Botは使われていません").queue()
            return
        }
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        musicManager.scheduler.shuffle()
        command.reply("再生キューをシャッフルしました").queue()
    }
}