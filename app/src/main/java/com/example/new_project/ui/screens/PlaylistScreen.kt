package com.example.new_project.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.new_project.ui.components.DeleteConfirmationDialog
import com.example.new_project.ui.search.TrackListItem
import com.example.new_project.ui.viewmodel.PlaylistViewModel
import com.example.new_project.data.utils.loadBitmapFromPath
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    playlistId: Long,
    viewModel: PlaylistViewModel,
    onBack: () -> Unit,
    onOpenTrackDetails: (String) -> Unit
) {
    val playlistState by viewModel.playlist.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTrackForDeletion by remember { mutableStateOf<com.example.new_project.domain.Track?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF2962FF)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(start = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Плейлист",
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp
                            )
                        )
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        error != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "Ошибка",
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = error!!,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Button(
                                        onClick = { viewModel.refreshPlaylist() }
                                    ) {
                                        Text("Повторить")
                                    }
                                }
                            }
                        }

                        playlistState == null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Плейлист не найден",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        playlistState?.tracks?.isEmpty() == true -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // ✅ ИЗМЕНЕНО: используем путь к файлу вместо URI
                                    PlaylistCoverImage(
                                        coverImagePath = playlistState?.coverImagePath,  // ✅ ИЗМЕНЕНО: coverImageUri → coverImagePath
                                        size = 150.dp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = playlistState?.name ?: "Плейлист",
                                        fontSize = 24.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                    Text(
                                        text = playlistState?.description ?: "",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Плейлист пуст",
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        else -> {
                            val playlist = playlistState!!

                            Column {
                                // ✅ ИЗМЕНЕНО: обложка и информация о плейлисте
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    PlaylistCoverImage(
                                        coverImagePath = playlist.coverImagePath,  // ✅ ИЗМЕНЕНО: coverImageUri → coverImagePath
                                        size = 150.dp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = playlist.name,
                                        fontSize = 24.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = playlist.description,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${playlist.tracks.size} треков",
                                        fontSize = 12.sp,
                                        color = Color.Gray.copy(alpha = 0.7f)
                                    )
                                }

                                Divider(
                                    thickness = 1.dp,
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(
                                        items = playlist.tracks,
                                        key = { it.id }
                                    ) { track ->
                                        TrackListItem(
                                            track = track,
                                            onClick = {
                                                onOpenTrackDetails(track.id)
                                            },
                                            onLongPress = {
                                                selectedTrackForDeletion = track
                                                showDeleteDialog = true
                                            }
                                        )
                                        HorizontalDivider(thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Snackbar для уведомлений
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Диалог удаления из плейлиста
        if (showDeleteDialog && selectedTrackForDeletion != null && playlistId > 0) {
            val currentTrack = selectedTrackForDeletion!!

            DeleteConfirmationDialog(
                title = "Удалить из плейлиста",
                message = "Вы уверены, что хотите удалить \"${currentTrack.trackName}\" из плейлиста?",
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedTrackForDeletion = null
                },
                onConfirmDelete = {
                    scope.launch {
                        try {
                            Log.d("PlaylistScreen", "Deleting track: ${currentTrack.trackName}")
                            val success = viewModel.deleteTrackFromPlaylist(currentTrack.id)

                            if (success) {
                                snackbarHostState.showSnackbar(
                                    message = "Трек '${currentTrack.trackName}' удален из плейлиста",
                                    withDismissAction = true
                                )
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "Не удалось удалить трек '${currentTrack.trackName}'",
                                    withDismissAction = true
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("PlaylistScreen", "Delete ERROR:", e)
                            snackbarHostState.showSnackbar(
                                message = "Ошибка удаления '${currentTrack.trackName}': ${e.message ?: "Неизвестная ошибка"}",
                                withDismissAction = true
                            )
                        } finally {
                            showDeleteDialog = false
                            selectedTrackForDeletion = null
                        }
                    }
                }
            )
        }
    }
}

// ✅ ИЗМЕНЕННЫЙ КОМПОНЕНТ: обложка плейлиста из файла
@Composable
fun PlaylistCoverImage(
    coverImagePath: String?,  // ✅ ИЗМЕНЕНО: coverImageUri → coverImagePath
    size: androidx.compose.ui.unit.Dp = 56.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Gray.copy(alpha = 0.1f))
    ) {
        if (!coverImagePath.isNullOrEmpty()) {
            // ✅ ИЗМЕНЕНО: загружаем Bitmap из файла
            val bitmap = loadBitmapFromPath(coverImagePath)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Обложка плейлиста",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                PlaceholderCover(size)
            }
        } else {
            PlaceholderCover(size)
        }
    }
}

@Composable
private fun PlaceholderCover(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = "Нет обложки",
            modifier = Modifier.size(size * 0.5f),
            tint = Color.Gray
        )
    }
}