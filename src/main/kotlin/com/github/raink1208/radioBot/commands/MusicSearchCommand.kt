package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import com.github.raink1208.radioBot.youtube.YoutubeAPIHandler
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@CommandDescription("search", "youtubeから動画を検索", ["se"], 1)
object MusicSearchCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val result =  YoutubeAPIHandler.search(args)
        val authorID = message.author.idLong
        val channelID = message.channel.idLong

        val embed = EmbedBuilder()
            .setTitle("検索結果")

        for (i in result.indices) {
            embed.addField((i + 1).toString(),
                "**タイトル**:\n `${result[i].title}` \n\n**videoID**: `${result[i].videoId}`",
                true
            )
        }

        message.channel.sendMessageEmbeds(embed.build()).queue()

        Main.instance.jda.eventManager.register(object : ListenerAdapter() {
            override fun onMessageReceived(event: MessageReceivedEvent) {
                if (event.message.author.idLong == authorID && event.channel.idLong == channelID) {
                    val select = event.message.contentRaw.toIntOrNull() ?: -1
                    if (0 < select && select <= result.size) {
                        MusicPlayCommand.execute(event.message, "https://www.youtube.com/watch?v="+result[select-1].videoId)
                    }
                    event.jda.eventManager.unregister(this)
                }
            }
        })
    }
}