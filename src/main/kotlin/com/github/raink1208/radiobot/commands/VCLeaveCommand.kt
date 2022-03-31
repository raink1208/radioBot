package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object VCLeaveCommand: CommandBase {
    override val commandData = Commands.slash("leave", "VCからBotを退出させる")

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        val connectionChannel = guild?.selfMember?.voiceState?.channel
        if (connectionChannel == null) {
            command.reply("VCに参加していません").queue()
            return
        }
        guild.audioManager.closeAudioConnection()
    }
}