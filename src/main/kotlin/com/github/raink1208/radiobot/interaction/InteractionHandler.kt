package com.github.raink1208.radiobot.interaction

class InteractionHandler {
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
}