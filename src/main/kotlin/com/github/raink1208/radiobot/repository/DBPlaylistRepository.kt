package com.github.raink1208.radiobot.repository

import com.github.raink1208.radiobot.model.Playlist
import com.github.raink1208.radiobot.util.Config
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.UUID

class DBPlaylistRepository: IPlaylistRepository {
    private val connection: Connection = DriverManager.getConnection(Config.getDBUrl(), Config.getDBUser(), Config.getDBPass())

    override fun save(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override fun delete(uuid: UUID) {
        val contentStmt = connection.prepareStatement("DELETE FROM playlist_contents WHERE playlist_id = UUID_TO_BIN(?)")
        contentStmt.setString(1, uuid.toString())
        contentStmt.execute()
        val stmt = connection.prepareStatement("DELETE FROM playlist WHERE id = UUID_TO_BIN(?)")
        stmt.setString(1, uuid.toString())
        stmt.execute()
    }

    override fun getEntirePlaylist(): List<Playlist> {
        TODO("Not yet implemented")
    }

    override fun existsPlaylist(playlistName: String): Boolean {
        val stmt = connection.prepareStatement("SELECT * FROM playlist WHERE name = ? LIMIT 1",
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
        stmt.setString(1, playlistName)
        val result = stmt.executeQuery()
        result.last()
        return result.row != 0
    }

    override fun find(playlistName: String): Playlist? {
        TODO("Not yet implemented")
    }
}