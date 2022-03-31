package com.github.raink1208.radiobot.listener

import com.github.raink1208.radiobot.command.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object CommandListener: ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        CommandHandler.execute(event)
    }
}