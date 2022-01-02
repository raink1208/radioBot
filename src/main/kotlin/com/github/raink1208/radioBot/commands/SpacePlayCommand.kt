package com.github.raink1208.radioBot.commands

import com.github.raink1208.radioBot.Main
import com.github.raink1208.radioBot.audio.GuildMusicManager
import com.github.raink1208.radioBot.command.CommandDescription
import com.github.raink1208.radioBot.command.ICommand
import com.github.raink1208.radioBot.models.TwitterUser
import com.github.raink1208.radioBot.space.SpaceAudioTrack
import com.github.raink1208.radioBot.space.TwitterAPIGateway
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager

@CommandDescription("space", "", [], 1)
object SpacePlayCommand: ICommand {
    override fun execute(message: Message, args: String) {
        val musicManager = Main.instance.getGuildAudioPlayer(message.guild)
        val twitterUser = TwitterAPIGateway.getUserByName(args)
        val audioTrack = createAudioTrack(twitterUser)

        val guild = message.guild
        val voiceChannel = message.member?.voiceState?.channel ?: return

        play(guild, voiceChannel, musicManager, audioTrack)
    }

    private fun createAudioTrack(user: TwitterUser): AudioTrack {
        val twitterSpace = TwitterAPIGateway.getSpaceByUserID(user.data.id)
        val audioSpace = TwitterAPIGateway.getAudioSpaceBySpaceID(twitterSpace.data[0].id)
        val streamStatus = TwitterAPIGateway.getSpaceStreamStatus(audioSpace.data.audioSpace.metadata.media_key)

        return SpaceAudioTrack(
            AudioTrackInfo(
                "space",
                "twitter space",
                Units.DURATION_MS_UNKNOWN,
                "",
                true,
                streamStatus.source.location
            )
        )
    }

    private fun play(guild: Guild, voiceChannel: VoiceChannel, musicManager: GuildMusicManager, track: AudioTrack) {
        connectVoiceChannel(guild.audioManager, voiceChannel)
        musicManager.scheduler.queue(track)
    }

    private fun connectVoiceChannel(audioManager: AudioManager, voiceChannel: VoiceChannel) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
    }
}