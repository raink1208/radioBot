package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object QueueLoopCommand: CommandBase {
    override val commandData = Commands.slash("queueloop", "再生キューに入ってる音楽をループ")

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使えません").queue()
            return
        }
        val audioChannel = command.member?.voiceState?.channel
        if (audioChannel == null) {
            command.reply("VCに参加してから使用してください").queue()
            return
        }
        val audioManager = guild.audioManager

        if (audioManager.connectedChannel?.id == audioChannel.id) {
            command.reply("他のチャンネルで使われています").queue()
        }
        val manager = Main.instance.getGuildAudioPlayer(guild)
        manager.queueLoop
        command.reply("キューループ: " + if (manager.queueLoop) "有効" else "無効" + "に切り替えました").queue()
    }
}