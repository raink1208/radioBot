package com.github.raink1208.radiobot.interaction.pageembed

import com.github.raink1208.radiobot.interaction.InteractionAction
import com.github.raink1208.radiobot.model.PageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ComponentInteraction
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button

class PageEmbedController(private val pageEmbed: PageEmbed, private val embedMessageId: Long): InteractionAction {
    companion object {
        fun createActionRows(): List<ItemComponent> {
            return listOf(
                Button.primary("prev", "◀︎"),
                Button.primary("next", "▶︎")
            )
        }
    }

    override fun interact(event: ComponentInteraction) {
        if (event is ButtonInteractionEvent) {
            when (event.componentId) {
                "prev" -> prev(event)
                "next" -> next(event)
            }
        }
    }

    fun prev(event: ButtonInteractionEvent) {
        event.deferEdit().queue()
        event.hook.editMessageEmbedsById(embedMessageId, pageEmbed.getPrev()).queue()
    }

    fun next(event: ButtonInteractionEvent) {
        event.deferEdit().queue()
        event.hook.editMessageEmbedsById(embedMessageId, pageEmbed.getNext()).queue()
    }
}