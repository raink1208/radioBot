package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.audio.GuildMusicManager
import com.github.raink1208.radiobot.command.CommandBase
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.managers.AudioManager

object MusicPlayCommand: CommandBase {
    override val commandData = Commands.slash("play", "音楽を再生する")
        .addOption(OptionType.STRING, "url", "再生する音楽のURL", true)

    override fun execute(command: SlashCommandInteraction) {
        val channel = command.channel
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
        val audioManager = guild.audioManager

        if (audioManager.isConnected) {
            if (audioManager.connectedChannel?.id != audioChannel.id) {
                channel.sendMessage("既にほかのチャンネルで使われています").queue()
                return
            }
        }

        val url = command.getOption("url")?.asString
        if (url == null) {
            command.reply("urlが入力されていません").queue()
            return
        }
        command.reply("再生の準備をしています").queue()
        loadAndPlay(channel, guild, audioChannel, url)
    }

    private fun loadAndPlay(channel: MessageChannel, guild: Guild, audioChannel: AudioChannel, trackUrl: String) {
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        Main.instance.playerManager.loadItemOrdered(musicManager, trackUrl, object :AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                channel.sendMessage("キューに曲を追加しました: " + track.info.title).queue()
                play(guild, audioChannel, musicManager, track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                for (audioTrack in playlist.tracks) {
                    play(guild, audioChannel, musicManager, audioTrack)
                }
                channel.sendMessage("キューにプレイリストを追加しました:" + playlist.name).queue()
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