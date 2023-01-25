package com.github.raink1208.radiobot.repository

import com.github.raink1208.radiobot.model.Playlist
import com.github.raink1208.radiobot.util.Config
import java.sql.Connection
import java.sql.DriverManager

class DBPlaylistRepository: IPlaylistRepository {
    private val connection: Connection = DriverManager.getConnection(Config.getDBUrl(), Config.getDBUser(), Config.getDBPass())

    override fun save(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override fun delete(playlistName: String) {
        TODO("Not yet implemented")
    }

    override fun getEntirePlaylist(): List<Playlist> {
        TODO("Not yet implemented")
    }

    override fun existsPlaylist(playlistName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun find(playlistName: String): Playlist? {
        TODO("Not yet implemented")
    }
}