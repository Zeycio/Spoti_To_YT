package com.zeycio.data.api

import com.zeycio.data.models.Playlist
import com.zeycio.data.models.Track
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface YouTubeMusicApi {
    @POST("playlists")
    suspend fun createPlaylist(@Body playlist: Playlist): Playlist

    @POST("playlists/{playlistId}/tracks")
    suspend fun addTrackToPlaylist(
        @Path("playlistId") playlistId: String,
        @Body track: Track
    ): Track
}