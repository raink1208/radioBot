package com.github.raink1208.radioBot.command

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

interface CommandBase {
    val commandData: SlashCommandData
    fun execute(command: SlashCommandInteraction)
}