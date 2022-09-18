package com.github.raink1208.radiobot.commands

import com.github.raink1208.radiobot.Main
import com.github.raink1208.radiobot.audio.AudioPlayer
import com.github.raink1208.radiobot.audiosource.space.SpaceAudioTrack
import com.github.raink1208.radiobot.audiosource.space.TwitterAPIGateway
import com.github.raink1208.radiobot.command.CommandBase
import com.github.raink1208.radiobot.model.TwitterUser
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands

class SpacePlayCommand: CommandBase {
    override val commandData = Commands.slash("space", "twitter spaceの再生")
        .addOption(OptionType.STRING, "twitter_id", "twitterID", true)

    override fun execute(command: SlashCommandInteraction) {
        val guild = command.guild
        if (guild == null) {
            command.reply("Guild外では使えません").queue()
            return
        }
        val musicManager = Main.instance.getGuildAudioPlayer(guild)
        val twitterId = command.getOption("twitter_id")?.asString
        if (twitterId == null) {
            command.reply("twitterIDが入力されていません").queue()
            return
        }
        val twitterUser = TwitterAPIGateway().getUserByName(twitterId)
        val audioTrack = createAudioTrack(twitterUser)

        val audioChannel = command.member?.voiceState?.channel
        if (audioChannel == null) {
            command.reply("VCに参加してから使用してください").queue()
            return
        }
        command.reply("再生の準備をしています").queue()
        AudioPlayer.play(guild, audioChannel, musicManager, audioTrack)
    }

    private fun createAudioTrack(user: TwitterUser): AudioTrack {
        val apiGateway = TwitterAPIGateway()
        val twitterSpace = apiGateway.getSpaceByUserID(user.data.id)
        val audioSpace = apiGateway.getAudioSpaceBySpaceID(twitterSpace.data[0].id)
        val streamStatus = apiGateway.getSpaceStreamStatus(audioSpace.data.audioSpace.metadata.media_key)

        return SpaceAudioTrack(
            AudioTrackInfo(
                "twitter space",
                user.data.username,
                Units.DURATION_MS_UNKNOWN,
                "",
                true,
                streamStatus.source.location
            )
        )
    }
}