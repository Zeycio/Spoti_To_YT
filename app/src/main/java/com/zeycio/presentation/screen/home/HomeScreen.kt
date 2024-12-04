package com.zeycio.presentation.screen.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.zeycio.MainActivity
import com.zeycio.data.models.Playlist


// presentation/screens/home/HomeScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activity: MainActivity,
    viewModel: HomeViewModel = hiltViewModel(),
    onPlaylistSelected: (Playlist) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val log by viewModel.log.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Transfer") },
                actions = {
                    IconButton(onClick = { /* Open settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
          val context  = LocalContext.current
            ServiceConnectionStatus(
                spotifyConnected = uiState.spotifyConnected,
                youtubeMusicConnected = uiState.youtubeMusicConnected,
                onSpotifyConnect = { viewModel.connectSpotify() },
                onYouTubeMusicConnect = {
                    activity.launchSignIn(
                        GoogleSignIn.getClient(
                            context,
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build()
                        ).signInIntent
                    )
                }
            )
            Text(
                text = if (uiState.youtubeMusicConnected)
                    "Connected to YouTube Music"
                else "Not Connect to YouTube Music"
            )
            if (log.isNotEmpty()) {
                Text(
                    text = log,
                    modifier = Modifier.padding(16.dp),
                    color = if (uiState.youtubeMusicConnected)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            PlaylistsList(
                playlists = uiState.playlists,
                onPlaylistClick = onPlaylistSelected
            )

            uiState.error?.let { error ->
               Text(text = error)
            }
        }
    }
}

@Composable
private fun ServiceConnectionStatus(
    spotifyConnected: Boolean,
    youtubeMusicConnected: Boolean,
    onSpotifyConnect: () -> Unit,
    onYouTubeMusicConnect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ServiceButton(
                service = "Spotify",
                connected = spotifyConnected,
                onClick = onSpotifyConnect
            )
            Spacer(modifier = Modifier.height(8.dp))
            ServiceButton(
                service = "YouTube Music",
                connected = youtubeMusicConnected,
                onClick = onYouTubeMusicConnect
            )
        }
    }
}

@Composable
private fun ServiceButton(
    service: String,
    connected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (connected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (connected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (connected) "Connected to $service"
            else "Connect to $service",
            color = if (connected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PlaylistsList(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit
) {
    LazyColumn {
        items(playlists) { playlist ->
            PlaylistItem(playlist = playlist, onClick = { onPlaylistClick(playlist) })
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(playlist.name) },
        supportingContent = { Text("${playlist.trackCount} tracks") },
        leadingContent = {
            AsyncImage(
                model = playlist.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}