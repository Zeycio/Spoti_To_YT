package com.zeycio.data.models

data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val trackCount: Int,
    val tracks: List<Track>,
    val imageUrl: String?
)

// data/models/Track.kt
data class Track(
    val id: String,
    val name: String,
    val artists: List<String>,
    val album: String?,
    val durationMs: Long
)

// data/models/TransferResult.kt
sealed class TransferResult {
    data class Success(
        val originalTrack: Track,
        val matchedTrackId: String
    ) : TransferResult()

    data class Failed(
        val originalTrack: Track,
        val reason: FailureReason
    ) : TransferResult()

    enum class FailureReason {
        NOT_FOUND,
        API_ERROR,
        RATE_LIMITED,
        UNKNOWN
    }
}

// presentation/screens/home/HomeUiState.kt
data class HomeUiState(
    val spotifyConnected: Boolean = false,
    val youtubeMusicConnected: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)