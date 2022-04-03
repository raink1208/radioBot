package com.github.raink1208.radiobot.interaction.selection

import com.github.raink1208.radiobot.service.PlaylistService
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button

object DeletePlaylist: SelectMenuInteractionBase {
    override val selectMenuId = "delete_playlist"

    override fun interact(event: SelectMenuInteractionEvent) {
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
            event.reply(playlist.name).addActionRow(Button.danger("delete_check", playlist.name + "を削除")).queue()
        }
    }
}