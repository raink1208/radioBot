package com.github.raink1208.radiobot.interaction

import net.dv8tion.jda.api.interactions.components.ComponentInteraction

interface InteractionAction {
    fun interact(event: ComponentInteraction)
}