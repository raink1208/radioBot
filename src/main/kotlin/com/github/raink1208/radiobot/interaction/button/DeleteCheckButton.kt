package com.github.raink1208.radiobot.interaction.button

import com.github.raink1208.radiobot.interaction.InteractionAction
import com.github.raink1208.radiobot.interaction.InteractionHandler
import com.github.raink1208.radiobot.model.Playlist
import com.github.raink1208.radiobot.service.PlaylistService
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ComponentInteraction

class DeleteCheckButton(private val playlist: Playlist): InteractionAction {
    override fun interact(event: ComponentInteraction) {
        if (event is ButtonInteractionEvent) {
            interact(event)
        }
        InteractionHandler.unregister(event.message.idLong)
    }

    private fun interact(event: ButtonInteractionEvent) {
        if (playlist.author != event.user.idLong) {
            event.reply("プレイリストの削除は作成した人しかできません").queue()
            return
        }
        if (PlaylistService.deletePlaylist(playlist.name, event.user)) {
            event.reply("playlist: ${playlist.name} を削除しました").queue()
        } else {
            event.reply("プレイリストの削除に失敗しました").queue()
        }
    }
}