package com.github.raink1208.radiobot.interaction

import net.dv8tion.jda.api.interactions.components.ComponentInteraction

object InteractionHandler {
    private val interactionActions = HashMap<Long, InteractionAction>()

    fun register(id: Long, action: InteractionAction) {
        interactionActions[id] = action
    }

    fun unregister(id: Long) {
        interactionActions.remove(id)
    }

    fun getInteractionAction(id: Long): InteractionAction? {
        return interactionActions[id]
    }

    fun interact(event: ComponentInteraction) {
        val action = getInteractionAction(event.message.idLong)
        if (action == null) {
            event.reply("有効期限切れです").queue()
            return
        }
        action.interact(event)
    }
}