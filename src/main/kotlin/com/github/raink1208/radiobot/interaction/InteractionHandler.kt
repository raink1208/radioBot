package com.github.raink1208.radiobot.interaction

import com.github.raink1208.radiobot.interaction.button.ButtonInteractionBase
import com.github.raink1208.radiobot.interaction.selection.PlayPlaylist
import com.github.raink1208.radiobot.interaction.selection.SelectMenuInteractionBase
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent

class InteractionHandler {
    private val selectMenuInteractions = mutableMapOf<String, SelectMenuInteractionBase>("play_playlist" to PlayPlaylist)
    private val buttonInteractions = mutableMapOf<String, ButtonInteractionBase>()

    fun interact(event: SelectMenuInteractionEvent) {
        selectMenuInteractions[event.selectMenu.id]?.interact(event)
    }

    fun interact(event: ButtonInteractionEvent) {
        buttonInteractions[event.button.id]?.interact(event)
    }
}