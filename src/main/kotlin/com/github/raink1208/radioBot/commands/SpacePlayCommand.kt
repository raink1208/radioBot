package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandBase
import com.github.raink1208.radioBot.models.TwitterUser
import com.github.raink1208.radioBot.space.SpaceAudioTrack
import com.github.raink1208.radioBot.space.TwitterAPIGateway
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.managers.AudioManager

object SpacePlayCommand: CommandBase {
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
        val twitterUser = TwitterAPIGateway.getUserByName(twitterId)
        val audioTrack = createAudioTrack(twitterUser)

        val audioChannel = command.member?.voiceState?.channel
        if (audioChannel == null) {
            command.reply("VCに参加してから使用してください").queue()
            return
        }
        play(guild, audioChannel, musicManager, audioTrack)
    }

    private fun createAudioTrack(user: TwitterUser): AudioTrack {
        val twitterSpace = TwitterAPIGateway.getSpaceByUserID(user.data.id)
        val audioSpace = TwitterAPIGateway.getAudioSpaceBySpaceID(twitterSpace.data[0].id)
        val streamStatus = TwitterAPIGateway.getSpaceStreamStatus(audioSpace.data.audioSpace.metadata.media_key)

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

    private fun play(guild: Guild, audioChannel: AudioChannel, musicManager: GuildMusicManager, track: AudioTrack) {
        connectVoiceChannel(guild.audioManager, audioChannel)
        musicManager.scheduler.queue(track)
    }

    private fun connectVoiceChannel(audioManager: AudioManager, audioChannel: AudioChannel) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(audioChannel)
        }
    }
}