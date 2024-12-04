package com.zeycio.domain

import com.zeycio.data.repo.SpotifyRepository
import com.zeycio.data.repo.YouTubeMusicRepository

import com.zeycio.data.models.Playlist
import com.zeycio.data.models.TransferResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject



class TransferPlaylist @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
    private val youtubeMusicRepository: YouTubeMusicRepository
) {
    operator fun invoke(playlistId: String): Flow<TransferResult> = flow {
        val playlist = spotifyRepository.getPlaylist(playlistId)
        val newPlaylist = youtubeMusicRepository.createPlaylist(playlist)


    }
}