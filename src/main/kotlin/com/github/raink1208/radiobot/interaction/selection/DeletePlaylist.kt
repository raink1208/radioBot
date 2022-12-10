package com.github.raink1208.radiobot.interaction.selection

import com.github.raink1208.radiobot.interaction.InteractionAction
import com.github.raink1208.radiobot.interaction.InteractionHandler
import com.github.raink1208.radiobot.interaction.button.DeleteCheckButton
import com.github.raink1208.radiobot.service.PlaylistService
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.ComponentInteraction
import net.dv8tion.jda.api.interactions.components.buttons.Button

class DeletePlaylist: InteractionAction {
    override fun interact(event: ComponentInteraction) {
        if (event is SelectMenuInteractionEvent) {
            interact(event)
        }
        InteractionHandler.unregister(event.message.idLong)
    }

    private fun interact(event: SelectMenuInteractionEvent) {
        for (selectedOption in event.selectedOptions) {
            val playlist = PlaylistService.getPlaylist(selectedOption.value)
            if (playlist == null) {
                event.reply("playlist: ${selectedOption.value} が見つかりませんでした").queue()
                return
            }
            if (playlist.author != event.user.idLong) {
                event.reply("プレイリストの削除は作成した人しかできません").queue()
                return
            }
            event.deferReply().queue {
                it.sendMessage("本当に削除しますか?").addActionRow(Button.danger("delete_check", playlist.name + "を削除")).queue { msg ->
                    InteractionHandler.register(msg.idLong, DeleteCheckButton(playlist))
                }
            }
        }
    }
}