package com.github.raink1208.radiobot.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PlaylistServiceTest {
    @Test
    fun checkName1() {
        assertTrue(PlaylistService.checkPlaylistName("sample"))
    }

    @Test
    fun checkName2() {
        assertFalse(PlaylistService.checkPlaylistName("?"))
    }

    @Test
    fun checkName3() {
        assertFalse(PlaylistService.checkPlaylistName("<"))
    }

    @Test
    fun checkName4() {
        assertFalse(PlaylistService.checkPlaylistName(">"))
    }

    @Test
    fun checkName5() {
        assertTrue(PlaylistService.checkPlaylistName("()"))
    }
}