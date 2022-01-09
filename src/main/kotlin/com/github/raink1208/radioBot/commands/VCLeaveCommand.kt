package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import net.dv8tion.jda.api.entities.Message

@CommandDescription("leave", "VCから退出する", ["l"])
object VCLeaveCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val connectionChannel = message.guild.selfMember.voiceState?.channel
        if (connectionChannel == null) {
            message.channel.sendMessage("VCに参加していません").queue()
            return
        }
        message.guild.audioManager.closeAudioConnection()
    }
}