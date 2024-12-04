package com.zeycio.data.repo



import com.zeycio.data.api.SpotifyApi
import com.zeycio.data.api.YouTubeMusicApi
import com.zeycio.data.models.Playlist
import com.zeycio.data.models.Track
import javax.inject.Inject

class YouTubeMusicRepository @Inject constructor(
    private val api: YouTubeMusicApi
) {
    suspend fun createPlaylist(playlist: Playlist): Playlist = api.createPlaylist(playlist)
    suspend fun addTrackToPlaylist(playlistId: String, track: Track): Track =
        api.addTrackToPlaylist(playlistId, track)
}