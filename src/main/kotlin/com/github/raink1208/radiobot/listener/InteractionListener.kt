package com.github.raink1208.radiobot.listener

import com.github.raink1208.radiobot.interaction.InteractionHandler
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object InteractionListener: ListenerAdapter() {
    override fun onSelectMenuInteraction(event: SelectMenuInteractionEvent) {
        InteractionHandler.interact(event)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        InteractionHandler.interact(event)
    }
}