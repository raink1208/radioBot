package com.github.raink1208.radiobot.listener

import com.github.raink1208.radiobot.interaction.InteractionHandler
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class InteractionListener: ListenerAdapter() {
    private val interactionHandler = InteractionHandler()
    override fun onSelectMenuInteraction(event: SelectMenuInteractionEvent) {
        interactionHandler.interact(event)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        interactionHandler.interact(event)
    }
}