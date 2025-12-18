package com.example.new_project.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.new_project.domain.Playlist
import com.example.new_project.ui.search.SearchViewModel
import com.example.new_project.ui.viewmodel.PlaylistsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    trackId: String,
    onBack: () -> Unit,
    searchViewModel: SearchViewModel,
    playlistsViewModel: PlaylistsViewModel
) {
    // Загружаем трек по ID при открытии экрана
    LaunchedEffect(trackId) {
        Log.d("TrackDetails", "Loading track: $trackId")
        searchViewModel.loadTrackById(trackId)
    }

    val trackState by searchViewModel.currentTrack.collectAsState()
    val playlists by playlistsViewModel.playlists.collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val track = trackState

    // Отладочный лог при изменении трека
    LaunchedEffect(track) {
        if (track != null) {
            Log.d("TrackDetails", "Track updated: ${track.trackName}, isFavorite: ${track.isFavorite}")
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF2962FF)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Шапка
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
                        "Детали трека",
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Основное содержимое
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (track == null) {
                        // Загрузка
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                        return@Column
                    }

                    // Большая обложка выше центра
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f) // Квадратная обложка
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray.copy(alpha = 0.1f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (track.artworkUrl != null) {
                            AsyncImage(
                                model = track.artworkUrl,
                                contentDescription = "Обложка альбома: ${track.trackName}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Плейсхолдер если нет обложки
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Нет обложки",
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Ряд с кнопкой лайка слева, названием и исполнителем по центру, кнопкой добавления справа
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Кнопка лайка (слева)
                        IconButton(
                            onClick = {
                                Log.d("TrackDetails", "=== FAVORITE BUTTON CLICKED ===")
                                Log.d("TrackDetails", "Current track: ${track.trackName}")
                                Log.d("TrackDetails", "Current isFavorite: ${track.isFavorite}")

                                val newFavoriteStatus = !track.isFavorite
                                Log.d("TrackDetails", "New favorite status: $newFavoriteStatus")

                                // 1. Сразу обновляем UI для мгновенной обратной связи
                                val updatedTrack = track.copy(isFavorite = newFavoriteStatus)
                                Log.d("TrackDetails", "Updated track - isFavorite: ${updatedTrack.isFavorite}")
                                searchViewModel.setCurrentTrack(updatedTrack)

                                // 2. Сохраняем в БД с ПРАВИЛЬНЫМ значением
                                scope.launch {
                                    try {
                                        Log.d("TrackDetails", "Calling updateTrackFavorite with isFavorite: $newFavoriteStatus")

                                        playlistsViewModel.updateTrackFavorite(updatedTrack, newFavoriteStatus)

                                        Log.d("TrackDetails", "Favorite saved to database")

                                        // 3. Небольшая задержка перед перезагрузкой
                                        kotlinx.coroutines.delay(100)

                                        // 4. Перезагружаем трек для синхронизации с БД
                                        searchViewModel.loadTrackById(trackId)
                                        Log.d("TrackDetails", "Track reloaded from DB")

                                    } catch (e: Exception) {
                                        Log.e("TrackDetails", "Error updating favorite: ${e.message}", e)

                                        // В случае ошибки возвращаем старое состояние
                                        Log.d("TrackDetails", "Reverting to old state")
                                        searchViewModel.setCurrentTrack(track)
                                    }
                                }
                                Log.d("TrackDetails", "=== BUTTON CLICK HANDLED ===")
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (track.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Избранное",
                                tint = if (track.isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Название и исполнитель (центр)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = track.trackName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = track.artistName,
                                fontSize = 18.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }

                        // Кнопка добавления в плейлист (справа)
                        IconButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                contentDescription = "Добавить в плейлист",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Дополнительная информация о треке
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Длительность
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Длительность",
                                modifier = Modifier.size(18.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Длительность: ${track.trackTime}",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }

                        // Альбом (если есть)
                        track.albumName?.let { albumName ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Album,
                                    contentDescription = "Альбом",
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Альбом: $albumName",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Snackbar для показа сообщений
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Bottom Sheet для выбора плейлиста
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Выбери плейлист",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    if (playlists.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Плейлистов пока нет",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(playlists) { playlist ->
                                PlaylistRow(
                                    playlist = playlist,
                                    onClick = {
                                        val currentTrack = trackState ?: return@PlaylistRow
                                        scope.launch {
                                            val result = playlistsViewModel.addTrackToPlaylistIfNotExists(
                                                track = currentTrack,
                                                playlistId = playlist.id
                                            )

                                            when (result) {
                                                is PlaylistsViewModel.AddTrackResult.Success -> {
                                                    snackbarHostState.showSnackbar(
                                                        message = "✅ Трек добавлен в плейлист \"${playlist.name}\"",
                                                        withDismissAction = true
                                                    )
                                                    sheetState.hide()
                                                    showBottomSheet = false
                                                }

                                                is PlaylistsViewModel.AddTrackResult.AlreadyExists -> {
                                                    snackbarHostState.showSnackbar(
                                                        message = "⚠️ Трек уже есть в плейлисте \"${playlist.name}\"",
                                                        withDismissAction = true
                                                    )
                                                    // Не закрываем bottom sheet, чтобы пользователь мог выбрать другой плейлист
                                                }

                                                is PlaylistsViewModel.AddTrackResult.Error -> {
                                                    snackbarHostState.showSnackbar(
                                                        message = "❌ Ошибка: ${result.message}",
                                                        withDismissAction = true
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistRow(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
                    playlist.tracks.isEmpty() -> "Нет треков"
                    playlist.tracks.size == 1 -> "1 трек"
                    playlist.tracks.size in 2..4 -> "${playlist.tracks.size} трека"
                    else -> "${playlist.tracks.size} треков"
                },
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = "Добавить в плейлист",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF2962FF)
        )
    }
}