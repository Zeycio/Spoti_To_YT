package com.zeycio.data.models

data class TransferState(
    val progress: Float = 0f,
    val transferredTracks: List<TransferResult> = emptyList(),
    val isComplete: Boolean = false,
    val error: String? = null
)

data class TransferScreenUiState(
    val playlist: Playlist? = null,
    val transferState: TransferState = TransferState(),
    val isLoading: Boolean = false
)