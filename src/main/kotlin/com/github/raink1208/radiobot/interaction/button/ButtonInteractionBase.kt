package com.github.raink1208.radiobot.interaction.button

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

interface ButtonInteractionBase {
    val buttonId: String
    fun interact(event: ButtonInteractionEvent)
}