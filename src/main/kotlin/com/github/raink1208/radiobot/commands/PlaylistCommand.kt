package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.audio.AudioPlayer
import com.github.raink1208.radiobot.command.CommandBase
import com.github.raink1208.radiobot.service.PlaylistService
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

object PlaylistCommand: CommandBase {
    private val createSubCommand = SubcommandData("create", "プレイリストの作成")
        .addOption(OptionType.STRING,"name", "プレイリスト名", true)
    private val deleteSubCommand = SubcommandData("delete", "プレイリストの削除")
        .addOption(OptionType.STRING, "name", "プレイリスト名", true)
    private val playSubcommand = SubcommandData("play", "プレイリストの再生")
        .addOption(OptionType.STRING,"name", "プレイリスト名", true)
    private val listSubCommand = SubcommandData("list", "登録されているプレイリスト一覧")
    private val editSubCommand = SubcommandData("edit", "プレイリストの編集")
        .addOption(OptionType.STRING,"name", "プレイリスト名", true)
    private val loadSubCommand = SubcommandData("load", "外部プレイリストの読み込み")
        .addOption(OptionType.STRING, "name", "プレイリスト名", true)
        .addOption(OptionType.STRING, "url", "プレイリストのURL", true)
    override val commandData = Commands.slash("playlist", "プレイリスト機能")
        .addSubcommands(createSubCommand, deleteSubCommand, playSubcommand, listSubCommand, editSubCommand, loadSubCommand)

    override fun execute(command: SlashCommandInteraction) {
        when(command.subcommandName) {
            "create" -> create(command)
            "delete" -> delete(command)
            "edit" -> edit(command)
            "list" -> list(command)
            "load" -> load(command)
            "play" -> play(command)
        }
    }

    private fun create(command: SlashCommandInteraction) {
        val name = command.getOption("name")?.asString
        if (name == null) {
            command.reply("nameが入力されていません").queue()
            return
        }
        if (PlaylistService.createPlaylist(name, command.user)) {
            command.reply("playlist: $name を作成しました").queue()
        } else {
            command.reply("playlist: $name は既に存在します").queue()
        }
    }

    private fun delete(command: SlashCommandInteraction) {
        val name = command.getOption("name")?.asString
        if (name == null) {
            command.reply("nameが入力されていません").queue()
            return
        }
        if (PlaylistService.deletePlaylist(name, command.user)) {
            command.reply("playlist: $name を削除しました").queue()
        } else {
            command.reply("playlist: $name の削除に失敗しました").queue()
        }
    }

    private fun play(command: SlashCommandInteraction) {
        val name = command.getOption("name")?.asString
        if (name == null) {
            command.reply("nameが入力されていません").queue()
            return
        }

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
                command.reply("既にほかのチャンネルで使われています").queue()
                return
            }
        }
        val playlist = PlaylistService.getPlaylist(name)
        if (playlist == null) {
            command.reply("playlist: $name が見つかりませんでした").queue()
            return
        }

        command.reply("プレイリストの読み込みを開始します").queue()
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        for (track in playlist.contents) {
            Main.instance.playerManager.loadItem(track.url, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    AudioPlayer.play(guild, audioChannel, musicManager, track)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    for (audioTrack in playlist.tracks) {
                        AudioPlayer.play(guild, audioChannel, musicManager, audioTrack)
                    }
                }

                override fun noMatches() {
                    command.channel.sendMessage("動画が見つかりませんでした: ${track.title}").queue()
                }

                override fun loadFailed(exception: FriendlyException) {
                    command.channel.sendMessage("読み込みできませんでした").queue()
                }
            })
        }
    }

    private fun edit(command: SlashCommandInteraction) {
        command.reply("この機能は準備中です").queue()
        //https://github.com/DV8FromTheWorld/JDA/pull/2024 TextInputの実装待ち
    }

    private fun list(command: SlashCommandInteraction) {
        val embed = EmbedBuilder()
        embed.setTitle("プレイリスト")
        for (playlist in PlaylistService.getEntirePlaylist()) {
            embed.addField(playlist.name, "author: <@" + playlist.author + ">", false)
        }
        command.replyEmbeds(embed.build()).queue()
    }

    private fun load(command: SlashCommandInteraction) {
        val name = command.getOption("name")?.asString
        if (name == null) {
            command.reply("nameが入力されていません").queue()
            return
        }
        val url = command.getOption("url")?.asString
        if (url == null) {
            command.reply("urlが入力されていません").queue()
            return
        }
        command.reply("プレイリストの読み込みを開始します").queue()
        PlaylistService.loadPlaylist(name, command.user, url)
    }
}