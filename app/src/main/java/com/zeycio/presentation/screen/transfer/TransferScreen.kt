package com.zeycio.presentation.screen.transfer



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.util.Logger
import com.zeycio.data.models.TransferResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    playlistId: String,
    onBackClick: () -> Unit,
    viewModel: TransferViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.playlist?.name ?: "Transfer Playlist") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.playlist?.let { playlist ->
                Text(
                    "Transferring ${playlist.trackCount} tracks",
                    modifier = Modifier.padding(16.dp)
                )
            }

            LinearProgressIndicator(
                progress = uiState.transferState.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn {
                items(uiState.transferState.transferredTracks) { result ->
                    when (result) {
                        is TransferResult.Success -> {
                            ListItem(
                                headlineContent = { Text(result.originalTrack.name) },
                                trailingContent = { Icon(Icons.Default.Check, "Success") }
                            )
                        }
                        is TransferResult.Failed -> {
                            ListItem(
                                headlineContent = { Text(result.originalTrack.name) },
                                trailingContent = { Icon(Icons.Default.Close, "Failed") }
                            )
                        }
                    }
                }
            }
        }
    }
}