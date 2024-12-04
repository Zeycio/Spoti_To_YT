package com.zeycio.domain

import com.zeycio.data.models.Playlist
import com.zeycio.data.repo.SpotifyRepository
import javax.inject.Inject

class GetSpotifyPlaylists @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    suspend operator fun invoke(): List<Playlist> = spotifyRepository.getUserPlaylists()
}