package com.github.raink1208.radioBot.listener

import com.github.raink1208.radioBot.command.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object CommandListener: ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        CommandHandler.execute(event)
    }
}