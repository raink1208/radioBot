package com.github.raink1208.radiobot.interaction.selection

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.audio.AudioPlayer
import com.github.raink1208.radiobot.service.PlaylistService
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.LayoutComponent

object PlayPlaylist: SelectMenuInteractionBase {
    override val selectMenuId = "play_playlist"

    override fun interact(event: SelectMenuInteractionEvent) {
        val components = ArrayList(event.message.actionRows)
        LayoutComponent.updateComponent(components, event.componentId, event.selectMenu)
        event.hook.editMessageComponentsById(event.messageId, components).queue()
        val guild = event.guild
        val audioChannel = event.member?.voiceState?.channel

        if (guild == null) {
            event.reply("Guild外では使用できません").queue()
            return
        }

        if (audioChannel == null) {
            event.reply("VC参加してから使用してください").queue()
            return
        }
        val audioManager = guild.audioManager

        if (audioManager.isConnected) {
            if (audioManager.connectedChannel?.id != audioChannel.id) {
                event.reply("既にほかのチャンネルで使われています").queue()
                return
            }
        }

        for (selectedOption in event.selectedOptions) {
            val playlist = PlaylistService.getPlaylist(selectedOption.value)
            if (playlist == null) {
                event.reply("playlist: ${selectedOption.value} が見つかりませんでした").queue()
                return
            }
            event.reply("playlist: ${playlist.name}の読み込みを開始します").queue()
            val player = AudioPlayer()
            for (track in playlist.contents) {
                val musicManager = Main.instance.getGuildAudioPlayer(guild)
                Main.instance.playerManager.loadItemOrdered(musicManager, track.url, object : AudioLoadResultHandler {
                    override fun trackLoaded(track: AudioTrack) {
                        player.play(guild, audioChannel, musicManager, track)
                    }

                    override fun playlistLoaded(playlist: AudioPlaylist) {
                        for (audioTrack in playlist.tracks) {
                            player.play(guild, audioChannel, musicManager, audioTrack)
                        }
                    }

                    override fun noMatches() {
                        event.channel.sendMessage("動画が見つかりませんでした: ${track.title}").queue()
                    }

                    override fun loadFailed(exception: FriendlyException) {
                        event.channel.sendMessage("読み込みできませんでした").queue()
                    }
                })
            }
        }
    }
}