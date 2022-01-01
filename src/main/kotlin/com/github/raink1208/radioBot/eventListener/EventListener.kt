package com.github.raink1208.radioBot.eventListener

import com.github.raink1208.radioBot.Main
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

object EventListener: ListenerAdapter() {
    val Log = LoggerFactory.getLogger(EventListener::class.java)
    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (event.entity.user.idLong == Main.instance.jda.selfUser.idLong) {
            Main.instance.removeGuildAudioPlayer(event.guild)
            Log.info("Voice Channelから切断されたのでmusicManagerが初期化されました")
        }
    }
}