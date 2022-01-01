package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import net.dv8tion.jda.api.entities.Message

@CommandDescription("loop", "再生している曲をループします", [])
object MusicLoopCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val channel = message.channel
        if (!Main.instance.existsGuildAudioPlayer(message.guild)) {
            channel.sendMessage("musicManagerが見つかりませんでした").queue()
            return
        }

        val voice = message.member?.voiceState?.channel
        if (voice == null) {
            channel.sendMessage("VCに参加してから使用してください").queue()
            return
        }
        val audioManager = message.guild.audioManager

        if (audioManager.connectedChannel?.id != voice.id) {
            channel.sendMessage("ほかのチャンネルで使われています").queue()
            return
        }

        val manager = Main.instance.getGuildAudioPlayer(message.guild)
        manager.toggleMusicLoop()
        channel.sendMessage("ループ: " + if (manager.musicLoop) "有効" else "無効" + "に切り替えました").queue()
    }
}