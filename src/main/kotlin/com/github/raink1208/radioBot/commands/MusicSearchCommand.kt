package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandBase
import com.github.raink1208.radioBot.youtube.YoutubeAPIHandler
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.managers.AudioManager

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
                        loadAndPlay(command.channel, guild, audioChannel, "https://www.youtube.com/watch?v="+result[select-1].videoId)
                    }
                    event.jda.eventManager.unregister(this)
                }
            }
        })
    }

    private fun loadAndPlay(channel: MessageChannel, guild: Guild, audioChannel: AudioChannel, trackUrl: String) {
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        Main.instance.playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                channel.sendMessage("キューに曲を追加したよ: " + track.info.title).queue()
                play(guild, audioChannel, musicManager, track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                var firstTrack = playlist.selectedTrack
                if (firstTrack == null) {
                    firstTrack = playlist.tracks[0]
                }
                channel.sendMessage("キューに曲を追加したよ: " + firstTrack.info.title + " (最初の曲: " + playlist.name + ")").queue()
                play(guild, audioChannel, musicManager, firstTrack)
            }

            override fun noMatches() {
                channel.sendMessage("動画が見つかりませんでした: $trackUrl").queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                channel.sendMessage("読み込みできませんでした").queue()
            }
        })
    }

    private fun play(guild: Guild, audioChannel: AudioChannel, musicManager: GuildMusicManager, track: AudioTrack) {
        connectVoiceChannel(guild.audioManager, audioChannel)
        musicManager.scheduler.queue(track)
    }

    private fun connectVoiceChannel(audioManager: AudioManager, audioChannel: AudioChannel) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(audioChannel)
        }
    }
}