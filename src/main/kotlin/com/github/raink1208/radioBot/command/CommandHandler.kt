package com.github.raink1208.radioBot.command

import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CommandHandler {
    private val logger: Logger = LoggerFactory.getLogger(CommandHandler::class.java)
    private val commandList = HashSet<ICommand>()

    fun registerCommands(commands: Set<ICommand>) {
        commandList.addAll(commands)
    }

    fun registerCommands(vararg commands: ICommand) {
        commandList.addAll(commands)
    }

    fun registerCommand(command: ICommand) {
        commandList.add(command)
    }

    fun unregisterCommands(commands: Set<ICommand>) {
        commandList.removeAll(commands)
    }

    fun unregisterCommands(vararg commands: ICommand) {
        commandList.removeAll(commands)
    }

    fun unregisterCommand(command: ICommand) {
        commandList.remove(command)
    }

    fun findCommand(command: String): ICommand? {
        return commandList.find {
            it.description?.aliases?.contains(command) ?: false || it.description?.name == command
        }
    }

    fun findAndExecute(command: String, message: Message, args: String) {
        val cmd = findCommand(command)
        if (cmd?.description == null) return
        execute(cmd, message, args)
    }

    fun execute(command: ICommand, message: Message, args: String) {
        val description = command.description

        if (description == null) {
            logger.error(command.javaClass.name + "クラスにDescriptionが存在しません")
            return
        }

        if (description.args > args.split(" ").size) return
        if (description.args == 1 && args == "") return
        args.trim()
        try {
            command.execute(message, args)
            logger.info("コマンドを実行しました: " + description.name)
        } catch (e: Exception) {
            logger.error("実行時にエラーが起きました: " + description.name)
            e.printStackTrace()
        }
    }

    fun getEntireCommands(): Set<ICommand> {
        return commandList
    }
}