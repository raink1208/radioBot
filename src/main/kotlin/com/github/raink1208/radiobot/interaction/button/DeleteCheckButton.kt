package com.github.raink1208.radiobot.interaction.button

import com.github.raink1208.radiobot.service.PlaylistService
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object DeleteCheckButton: ButtonInteractionBase {
    override val buttonId = "delete_check"

    override fun interact(event: ButtonInteractionEvent) {
        val playlist = PlaylistService.getPlaylist(event.message.contentRaw)
        if (playlist == null) {
            event.reply("playlist: ${event.message.contentRaw} が見つかりませんでした").queue()
            return
        }
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