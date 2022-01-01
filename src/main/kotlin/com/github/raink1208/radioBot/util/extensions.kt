package com.github.raink1208.radioBot.util

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

fun AudioTrack.infoEmbed(): MessageEmbed {
    return EmbedBuilder()
        .setTitle("曲の情報")
        .addField("タイトル", this.info.title, false)
        .addField("投稿者", this.info.author, false)
        .addField("URL", this.info.uri, false)
        .build()
}
