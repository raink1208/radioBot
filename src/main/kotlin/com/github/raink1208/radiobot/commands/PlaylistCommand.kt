package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.command.CommandBase
import com.github.raink1208.radiobot.service.PlaylistService
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
        .addOption(OptionType.STRING,"url", "プレイリストのURL", true)
    override val commandData = Commands.slash("playlist", "プレイリスト機能")
        .addSubcommands(createSubCommand, deleteSubCommand, playSubcommand, listSubCommand, editSubCommand, loadSubCommand)

    override fun execute(command: SlashCommandInteraction) {
        when(command.subcommandName) {
            "create" -> create(command)
            "delete" -> delete(command)
            "edit" -> edit(command)
            "list" -> list(command)
        }
    }

    private fun create(command: SlashCommandInteraction) {
        val name = command.getOption("name")?.asString
        if (name == null) {
            command.reply("nameが入力されていません").queue()
            return
        }
        if (PlaylistService.createPlaylist(name, command.user.idLong)) {
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
}