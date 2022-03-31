package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object MusicLoopCommand: CommandBase {
    override val commandData = Commands.slash("loop", "再生してる音楽をループする")
    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使用できません").queue()
            return
        }
        val audioChannel = command.member?.voiceState?.channel
        if (audioChannel == null) {
            command.reply("VCに参加してから使用してください").queue()
            return
        }
        if (!Main.instance.existsGuildAudioPlayer(guild)) {
            command.reply("musicManagerが見つかりません")
            return
        }
        val audioManager = guild.audioManager
        if (audioManager.connectedChannel?.id != audioChannel.id) {
            command.reply("他のチャンネルで使われています").queue()
            return
        }
        val manager = Main.instance.getGuildAudioPlayer(guild)
        manager.toggleMusicLoop()
        command.reply("ループ: " + if (manager.musicLoop) "有効" else "無効" + "に切り替えました").queue()
    }
}