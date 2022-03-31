package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.audio.AudioPlayer
import com.github.raink1208.radiobot.command.CommandBase
import com.github.raink1208.radiobot.youtube.YoutubeAPIHandler
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

object MusicSearchCommand: CommandBase {
    override val commandData = Commands.slash("search", "youtubeから動画を検索")
        .addOption(OptionType.STRING,"words", "検索するワード", true)

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        val audioChannel = command.member?.voiceState?.channel
        if (guild == null) {
            command.reply("Guild外では使用できません").queue()
            return
        }
        if (audioChannel == null) {
            command.reply("VC参加してから使用してください").queue()
            return
        }

        val words = command.getOption("words")?.asString
        if (words == null) {
            command.reply("検索ワードを入力してください").queue()
            return
        }
        val result = YoutubeAPIHandler.search(words)
        val authorId = command.user.idLong
        val channelId = command.channel.idLong

        val embed = EmbedBuilder()
            .setTitle("検索結果")

        for (i in result.indices) {
            embed.addField((i + 1).toString(),
                "**タイトル**:\n `${result[i].title}` \n\n**videoID**: `${result[i].videoId}`",
                true
            )
        }

        command.replyEmbeds(embed.build())

        Main.instance.jda.eventManager.register(object : ListenerAdapter() {
            override fun onMessageReceived(event: MessageReceivedEvent) {
                if (event.message.author.idLong == authorId && event.channel.idLong == channelId) {
                    val select = event.message.contentRaw.toIntOrNull() ?: -1
                    if (0 < select && select <= result.size) {
                        AudioPlayer.loadAndPlay(command.channel, guild, audioChannel, "https://www.youtube.com/watch?v="+result[select-1].videoId)
                    }
                    event.jda.eventManager.unregister(this)
                }
            }
        })
    }
}