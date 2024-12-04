package com.zeycio.presentation.screen.transfer



import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeycio.data.models.TransferScreenUiState
import com.zeycio.domain.TransferPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferPlaylist: TransferPlaylist,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransferScreenUiState())
    val uiState: StateFlow<TransferScreenUiState> = _uiState.asStateFlow()

 

    private fun startTransfer(playlistId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            transferPlaylist(playlistId)
                .catch { error ->
                    _uiState.update { it.copy(
                        transferState = it.transferState.copy(error = error.message),
                        isLoading = false
                    ) }
                }
                .collect { result ->
                    _uiState.update { state ->
                        val newTransferredTracks = state.transferState.transferredTracks + result
                        state.copy(
                            transferState = state.transferState.copy(
                                transferredTracks = newTransferredTracks,
                                progress = newTransferredTracks.size.toFloat() /
                                        (state.playlist?.trackCount ?: 1).toFloat(),
                                isComplete = newTransferredTracks.size ==
                                        state.playlist?.trackCount
                            )
                        )
                    }
                }
        }
    }
}