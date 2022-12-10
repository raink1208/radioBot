package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.command.CommandBase
import com.github.raink1208.radiobot.interaction.InteractionHandler
import com.github.raink1208.radiobot.interaction.pageembed.PageEmbedController
import com.github.raink1208.radiobot.model.PageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

class MusicQueueCommand: CommandBase {
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
        val pageEmbedBuilder = PageEmbed.PageEmbedBuilder()
        pageEmbedBuilder.title = "再生リスト"
        for ((i, audioTrack) in audioTrackList.withIndex()) {
            pageEmbedBuilder.addField(MessageEmbed.Field(i.toString()+"."+audioTrack.info.title, audioTrack.info.author, true))
        }
        val pageEmbed = pageEmbedBuilder.build()
        command.deferReply().queue {
            it.sendMessageEmbeds(pageEmbed.getEmbed()).addActionRow(PageEmbedController.createActionRows()).queue { msg ->
                InteractionHandler.register(msg.idLong, PageEmbedController(pageEmbed, msg.idLong))
            }
        }
    }
}