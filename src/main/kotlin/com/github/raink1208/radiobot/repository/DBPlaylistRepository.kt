package com.github.raink1208.radiobot.repository

import com.github.raink1208.radiobot.model.Playlist
import com.github.raink1208.radiobot.model.PlaylistItem
import com.github.raink1208.radiobot.util.Config
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.UUID

class DBPlaylistRepository: IPlaylistRepository {
    private val connection: Connection = DriverManager.getConnection(Config.getDBUrl(), Config.getDBUser(), Config.getDBPass())

    override fun save(playlist: Playlist) {
        val stmt = connection.prepareStatement("INSERT INTO playlist (id, name, author_id, guild_id, is_public, upstream) VALUES " +
                "(UUID_TO_BIN(?), ?, ?, ?, ?, ?)")
        stmt.setString(1, playlist.uuid.toString())
        stmt.setString(2, playlist.name)
        stmt.setLong(3, playlist.author)
        stmt.setLong(4, playlist.guildId)
        stmt.setBoolean(5, playlist.isPublic)
        stmt.setString(6, playlist.upstream)
        stmt.execute()

        val contentStmt = connection.prepareStatement("INSERT INTO playlist_contents(playlist_id,title, url) VALUES (UUID_TO_BIN(?), ?, ?)")
        for (content in playlist.contents) {
            contentStmt.setString(1, playlist.uuid.toString())
            contentStmt.setString(2, content.title)
            contentStmt.setString(3, content.url)
            contentStmt.execute()
        }
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
        val stmt = connection.prepareStatement("SELECT BIN_TO_UUID(id) as id, name, author_id, guild_id, is_public, upstream FROM playlist WHERE name = ?")
        stmt.setString(1, playlistName)
        val result = stmt.executeQuery()
        while (result.next()) {
            val uuid = UUID.fromString(result.getString("id"))
            val name = result.getString("name")
            val authorId = result.getLong("author_id")
            val guildId = result.getLong("guild_id")
            val isPublic = result.getBoolean("is_public")
            val upstream = result.getString("upstream")
            val contents = loadContents(uuid)
            return Playlist(uuid, name, authorId, isPublic, upstream, guildId, contents)
        }
        return null
    }

    private fun loadContents(playlistId: UUID): MutableList<PlaylistItem> {
        val stmt = connection.prepareStatement("SELECT * FROM playlist_contents WHERE playlist_id = UUID_TO_BIN(?)")
        stmt.setString(1, playlistId.toString())
        val result = stmt.executeQuery()
        val list = mutableListOf<PlaylistItem>()
        while (result.next()) {
            val title = result.getString("title")
            val url = result.getString("url")
            list.add(PlaylistItem(title, url))
        }
        return list
    }
}