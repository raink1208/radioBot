package com.github.raink1208.radiobot.listener

import com.github.raink1208.radiobot.Main
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EventListener: ListenerAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(EventListener::class.java)
    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (event.entity.user.idLong == Main.instance.jda.selfUser.idLong) {
            Main.instance.removeGuildAudioPlayer(event.guild)
            logger.info("Voice Channelから切断されたのでmusicManagerが初期化されました")
        }
    }
}