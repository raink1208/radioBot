package com.github.raink1208.radiobot.interaction.selection

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent

interface SelectMenuInteractionBase {
    val selectMenuId: String
    fun interact(event: SelectMenuInteractionEvent)
}