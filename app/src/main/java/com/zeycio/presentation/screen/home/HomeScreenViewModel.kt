package com.zeycio.presentation.screen.home




import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.zeycio.MainActivity
import com.zeycio.data.auth.AuthManager
import com.zeycio.data.models.HomeUiState
import com.zeycio.domain.GetSpotifyPlaylists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val getSpotifyPlaylists: GetSpotifyPlaylists
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val _log = MutableStateFlow("")
    val log = _log.asStateFlow()
    init {
        refreshAuthState()
    }

    fun connectSpotify() {
        authManager.startSpotifyAuth()
    }



    fun onAuthError(error: String) {
        _log.update { "Auth Error: $error" }
        _uiState.update { it.copy(error = error) }
    }


    fun refreshAuthState() {
        _uiState.update { it.copy(
            spotifyConnected = authManager.isSpotifyConnected(),
            youtubeMusicConnected = authManager.isYouTubeConnected()
        ) }
        if (authManager.isSpotifyConnected()) {
            loadPlaylists()
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val playlists = getSpotifyPlaylists()
                _uiState.update { it.copy(
                    playlists = playlists,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message,
                    isLoading = false
                ) }
            }
        }
    }



    fun onGoogleSignInSuccess(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _log.value = "Sign in successful: ${account.email}"
            try {
//                val playlists = youtubeMusicRepository.getPlaylists(account)
                _uiState.update { it.copy(
                     youtubeMusicConnected = true,
//                    playlists = playlists
                )}
            } catch (e: Exception) {
                _log.value = "Failed to fetch playlists: ${e.message}"
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onSignInError(error: String) {
        _log.value = "Sign in failed: $error"
        _uiState.update { it.copy(
            youtubeMusicConnected = false,
            error = error
        )}
    }
}