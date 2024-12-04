package com.zeycio.data.api

import com.zeycio.data.models.Playlist
import retrofit2.http.GET
import retrofit2.http.Path

interface SpotifyApi {
    @GET("users/{userId}/playlists")
    suspend fun getUserPlaylists(): List<Playlist>

    @GET("playlists/{playlistId}")
    suspend fun getPlaylist(@Path("playlistId") playlistId: String): Playlist
}

