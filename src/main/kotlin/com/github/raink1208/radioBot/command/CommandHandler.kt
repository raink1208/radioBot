package com.github.raink1208.radioBot.command

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object CommandHandler {
    private val logger: Logger = LoggerFactory.getLogger(CommandHandler::class.java)
    private val commandList = HashSet<CommandBase>()

    fun registerCommand(command: CommandBase) {
        logger.info("Loaded "+command.commandData.name + "Command")
        commandList.add(command)
    }

    fun unregisterCommand(command: CommandBase) {
        commandList.remove(command)
    }

    fun findCommand(commandName: String): CommandBase? {
        return commandList.find {
            it.commandData.name == commandName
        }
    }

    fun execute(commandInteraction: SlashCommandInteraction) {
        val command = findCommand(commandInteraction.name)
        if (command == null) {
            commandInteraction.reply("コマンドが見つかりませんでした").queue()
            return
        }
        try {
            command.execute(commandInteraction)
            logger.info("コマンドを実行しました: " + command.commandData.name)
        } catch (e: Exception) {
            logger.error("実行時にエラーが起きました: " + command.commandData.name)
            e.printStackTrace()
        }
    }

    fun getEntireCommandList(): Set<CommandBase> {
        return commandList
    }
}