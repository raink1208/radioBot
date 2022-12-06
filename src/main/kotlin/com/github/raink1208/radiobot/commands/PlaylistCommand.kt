package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.command.CommandBase
import com.github.raink1208.radiobot.interaction.InteractionHandler
import com.github.raink1208.radiobot.interaction.selection.DeletePlaylist
import com.github.raink1208.radiobot.interaction.selection.PlayPlaylist
import com.github.raink1208.radiobot.service.PlaylistService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu

class PlaylistCommand: CommandBase {
    private val deleteSubCommand = SubcommandData("delete", "プレイリストの削除")
    private val playSubCommand = SubcommandData("play", "プレイリストの再生")
    private val listSubCommand = SubcommandData("list", "登録されているプレイリスト一覧")
    private val loadSubCommand = SubcommandData("load", "外部プレイリストの読み込み")
        .addOption(OptionType.STRING, "name", "プレイリスト名", true)
        .addOption(OptionType.STRING, "url", "プレイリストのURL", true)
    override val commandData = Commands.slash("playlist", "プレイリスト機能")
        .addSubcommands(deleteSubCommand, playSubCommand, listSubCommand, loadSubCommand)

    override fun execute(command: SlashCommandInteraction) {
        when(command.subcommandName) {
            "delete" -> delete(command)
            "list" -> list(command)
            "load" -> load(command)
            "play" -> play(command)
        }
    }

    private fun play(command: SlashCommandInteraction) {
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
        val list = PlaylistService.getPlaylistFindByGuild(guild)
        if (list.isEmpty()) {
            command.reply("再生可能なプレイリストが存在しません").queue()
            return
        }

        val selectMenu = SelectMenu.create("play_playlist")
        for (playlist in list) {
            selectMenu.addOption(playlist.name, playlist.name)
        }
        command.deferReply().queue {
            it.sendMessage("再生するリストを選択してください").addActionRow(selectMenu.build()).queue { msg ->
                InteractionHandler.register(msg.idLong, PlayPlaylist())
            }
        }
    }

    private fun delete(command: SlashCommandInteraction) {
        val list = PlaylistService.getPlaylistFindByUser(command.user)
        if (list.isEmpty()) {
            command.reply("選択可能なプレイリストが存在しません").queue()
            return
        }

        val selectMenu = SelectMenu.create("delete_playlist")
        for (playlist in list) {
            selectMenu.addOption(playlist.name, playlist.name)
        }

        command.deferReply().queue {
            it.sendMessage("削除するプレイリストを選択してください").addActionRow(selectMenu.build()).queue { msg ->
                InteractionHandler.register(msg.idLong, DeletePlaylist())
            }
        }
    }

    private fun list(command: SlashCommandInteraction) {
        val guild = command.guild

        if (guild == null) {
            command.reply("Guild外では使用できません").queue()
            return
        }

        val embed = EmbedBuilder()
        embed.setTitle("プレイリスト")
        for (playlist in PlaylistService.getPlaylistFindByGuild(guild)) {
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
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使用できません").queue()
            return
        }
        command.reply("プレイリストの読み込みを開始します").queue()
        when(PlaylistService.loadPlaylist(name, command.user, guild, url)) {
            PlaylistService.CreatePlaylist.SUCCESS -> {
                command.channel.sendMessage("playlist: $name を作成しました").queue()
            }
            PlaylistService.CreatePlaylist.NAME_EXISTS -> {
                command.channel.sendMessage("playlist: $name は既に存在します").queue()
            }
            PlaylistService.CreatePlaylist.NAME_ERROR -> {
                command.channel.sendMessage("使えない文字が入っています").queue()
            }
        }
    }
}