package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.command.CommandBase
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

object PlaylistCommand: CommandBase {
    private val createSubCommand = SubcommandData("create", "プレイリストの作成")
        .addOption(OptionType.STRING,"name", "プレイリスト名", true)
    private val playSubcommand = SubcommandData("play", "プレイリストの再生")
        .addOption(OptionType.STRING,"name", "プレイリスト名", true)
    private val listSubCommand = SubcommandData("list", "登録されているプレイリスト一覧")
    private val editSubCommand = SubcommandData("edit", "プレイリストの編集")
        .addOption(OptionType.STRING,"name", "プレイリスト名", true)
    private val loadSubCommand = SubcommandData("load", "外部プレイリストの読み込み")
        .addOption(OptionType.STRING,"url", "プレイリストのURL", true)
    override val commandData = Commands.slash("playlist", "プレイリスト機能")
        .addSubcommands(createSubCommand, playSubcommand, listSubCommand, editSubCommand, loadSubCommand)

    override fun execute(command: SlashCommandInteraction) {
    }
}