package com.github.raink1208.radiobot.interaction

import com.github.raink1208.radiobot.interaction.button.ButtonInteractionBase
import com.github.raink1208.radiobot.interaction.selection.SelectMenuInteractionBase
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.ComponentInteraction

object InteractionHandler {
    private val selectMenuInteractions = mutableMapOf<String, SelectMenuInteractionBase>()
    private val buttonInteractions = mutableMapOf<String, ButtonInteractionBase>()

    fun interact(event: ComponentInteraction) {
        if (event is SelectMenuInteractionEvent) {
            selectMenuInteractions[event.selectMenu.id]?.interact(event)
        } else if (event is ButtonInteractionEvent) {
            buttonInteractions[event.button.id]?.interact(event)
        }
    }
}