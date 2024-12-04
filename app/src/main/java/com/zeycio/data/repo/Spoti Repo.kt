package com.zeycio.data.repo

import com.zeycio.data.api.SpotifyApi
import com.zeycio.data.models.Playlist
import javax.inject.Inject


class SpotifyRepository @Inject constructor(
    private val api: SpotifyApi
) {
    suspend fun getUserPlaylists(): List<Playlist> = api.getUserPlaylists()
    suspend fun getPlaylist(playlistId: String): Playlist = api.getPlaylist(playlistId)
}
