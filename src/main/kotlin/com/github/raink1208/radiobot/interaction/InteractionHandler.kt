package com.github.raink1208.radiobot.interaction

import com.github.raink1208.radiobot.util.TimeUtil
import net.dv8tion.jda.api.interactions.components.ComponentInteraction
import java.util.Timer
import java.util.TimerTask

object InteractionHandler {
    private val interactionActions = HashMap<Long, InteractionAction>()
    private val timer = Timer(true)

    fun register(id: Long, action: InteractionAction) {
        interactionActions[id] = action
        val task = object : TimerTask() {
            override fun run() {
                unregister(id)
                cancel()
            }
        }
        timer.schedule(task, TimeUtil.toMs(10,0))
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