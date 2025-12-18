package com.example.new_project.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.new_project.domain.Track
import com.example.new_project.ui.components.DeleteConfirmationDialog
import com.example.new_project.ui.viewmodel.PlaylistsViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onOpenTrackDetails: (String) -> Unit,
    viewModel: PlaylistsViewModel
) {
    val favorites by viewModel.favoriteList.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTrackForDeletion by remember { mutableStateOf<Track?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF2962FF)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Синяя шапка
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
                            text = "Избранное",
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Белая карточка с содержимым
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
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (favorites.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Нет избранных треков",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp)
                            ) {
                                items(favorites) { track ->
                                    FavoriteTrackItem(
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

                // Snackbar для уведомлений
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // Диалог удаления из избранного
            // Диалог удаления из избранного
            if (showDeleteDialog && selectedTrackForDeletion != null) {
                val currentTrack = selectedTrackForDeletion!!  // Сохраняем в локальную переменную

                DeleteConfirmationDialog(
                    title = "Удалить из избранного",
                    message = "Вы уверены, что хотите удалить \"${currentTrack.trackName}\" из избранного?",
                    onDismissRequest = {
                        showDeleteDialog = false
                        selectedTrackForDeletion = null
                    },
                    onConfirmDelete = {
                        scope.launch {
                            try {
                                Log.d("FavoritesScreen", "=== START DELETE FROM FAVORITES ===")
                                Log.d("FavoritesScreen", "Deleting track: ${currentTrack.trackName}")
                                Log.d("FavoritesScreen", "Track ID: ${currentTrack.id}")

                                viewModel.updateTrackFavorite(currentTrack, false)

                                Log.d("FavoritesScreen", "✅ Track removed from favorites")

                                snackbarHostState.showSnackbar(
                                    message = "Трек '${currentTrack.trackName}' удален из избранного",
                                    withDismissAction = true
                                )
                            } catch (e: Exception) {
                                Log.e("FavoritesScreen", "❌ Error deleting from favorites:", e)
                                Log.e("FavoritesScreen", "Error message: ${e.message}")
                                Log.e("FavoritesScreen", "Error stack:")
                                e.printStackTrace()

                                snackbarHostState.showSnackbar(
                                    message = "Ошибка: ${e.message ?: "Не удалось удалить трек из избранного"}",
                                    withDismissAction = true
                                )
                            } finally {
                                showDeleteDialog = false
                                selectedTrackForDeletion = null
                                Log.d("FavoritesScreen", "=== END DELETE FROM FAVORITES ===")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FavoriteTrackItem(
    track: Track,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.artistName,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = track.trackTime,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}