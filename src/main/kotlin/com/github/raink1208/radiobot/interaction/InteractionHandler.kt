package com.github.raink1208.radiobot.interaction

import com.github.raink1208.radiobot.interaction.button.ButtonInteractionBase
import com.github.raink1208.radiobot.interaction.button.DeleteCheckButton
import com.github.raink1208.radiobot.interaction.selection.DeletePlaylist
import com.github.raink1208.radiobot.interaction.selection.PlayPlaylist
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent

class InteractionHandler {
    private val selectMenuInteractions = mutableMapOf(
        "play_playlist" to PlayPlaylist,
        "delete_playlist" to DeletePlaylist
    )
    private val buttonInteractions = mutableMapOf<String, ButtonInteractionBase>(
        "delete_check" to DeleteCheckButton
    )

    fun interact(event: SelectMenuInteractionEvent) {
        selectMenuInteractions[event.selectMenu.id]?.interact(event)
    }

    fun interact(event: ButtonInteractionEvent) {
        buttonInteractions[event.button.id]?.interact(event)
    }
}