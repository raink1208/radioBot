package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager

@CommandDescription("play", "音楽を再生する", ["p", "pl"], 1)
object MusicPlayCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val channel = message.channel
        val voiceChannel = message.member?.voiceState?.channel

        if (voiceChannel == null) {
            channel.sendMessage("VCに参加してから使用してください").queue()
            return
        }

        val audioManager = message.guild.audioManager

        if (audioManager.isConnected) {
            if (audioManager.connectedChannel?.id != voiceChannel.id) {
                channel.sendMessage("既にほかのチャンネルで使われています").queue()
                return
            }
        }

        if (voiceChannel is VoiceChannel)
        if (channel is TextChannel) loadAndPlay(channel, voiceChannel, args)
    }

    private fun loadAndPlay(channel: TextChannel, voiceChannel: VoiceChannel, trackUrl: String) {
        val musicManager = Main.instance.getGuildAudioPlayer(channel.guild)
        Main.instance.playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                channel.sendMessage("キューに曲を追加したよ: " + track.info.title).queue()
                play(channel.guild, voiceChannel, musicManager, track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                var firstTrack = playlist.selectedTrack
                if (firstTrack == null) {
                    firstTrack = playlist.tracks[0]
                }
                channel.sendMessage("キューに曲を追加したよ: " + firstTrack!!.info.title + " (最初の曲: " + playlist.name + ")").queue()
                play(channel.guild, voiceChannel, musicManager, firstTrack)
            }

            override fun noMatches() {
                channel.sendMessage("動画が見つかりませんでした: $trackUrl").queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                channel.sendMessage("読み込みできませんでした").queue()
            }
        })
    }

    private fun play(guild: Guild, voiceChannel: VoiceChannel, musicManager: GuildMusicManager, track: AudioTrack) {
        connectVoiceChannel(guild.audioManager, voiceChannel)
        musicManager.scheduler.queue(track)
    }

    private fun connectVoiceChannel(audioManager: AudioManager, voiceChannel: VoiceChannel) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
    }
}