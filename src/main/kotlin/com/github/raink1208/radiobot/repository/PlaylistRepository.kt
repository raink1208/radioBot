package com.github.raink1208.radiobot.repository

import com.github.raink1208.radiobot.model.Playlist
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.file.Files
import java.nio.file.Paths

class PlaylistRepository {
    private val format = Json { encodeDefaults = true }
    private val path = Paths.get("./playlists").toAbsolutePath()
    init {
        if (!Files.exists(path))
            Files.createDirectory(path)
    }

    fun save(playlist: Playlist) {
        val file = Paths.get(path.toString(), playlist.name + ".json").toFile()
        if (exists(file)) {
            file.createNewFile()
        }
        val outputStreamWriter = OutputStreamWriter(FileOutputStream(file))
        val bufferedWriter = BufferedWriter(outputStreamWriter)
        bufferedWriter.write(format.encodeToString(playlist))
        bufferedWriter.close()
    }

    fun existsPlaylist(playlistName: String): Boolean {
        return exists(Paths.get(path.toString(), "$playlistName.json").toFile())
    }

    private fun exists(file: File): Boolean {
        return file.exists()
    }

    fun find(playlistName: String): Playlist? {
        val file = Paths.get(path.toString(), "$playlistName.json").toFile()
        if (!exists(file)) return null
        return format.decodeFromString<Playlist>(file.readText())
    }
}