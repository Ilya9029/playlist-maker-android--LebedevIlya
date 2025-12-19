package com.example.new_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.new_project.domain.Playlist
import com.example.new_project.ui.components.PlaylistCoverImage
import com.example.new_project.ui.viewmodel.PlaylistsViewModel

@Composable
fun PlaylistsScreen(
    onBack: () -> Unit,
    onOpenNewPlaylist: () -> Unit,
    onOpenPlaylist: (Long) -> Unit,
    viewModel: PlaylistsViewModel
) {
    val playlists by viewModel.playlists.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2962FF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Синяя шапка с заголовком
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Плейлисты",
                    fontSize = 22.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Белая карточка со списком плейлистов
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
            ) {
                if (playlists.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Нет плейлистов",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Создайте первый плейлист",
                                fontSize = 14.sp,
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        items(items = playlists, key = { it.id }) { playlist ->
                            PlaylistListItem(
                                playlist = playlist,
                                onClick = {
                                    onOpenPlaylist(playlist.id)
                                }
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }

        // Плавающая кнопка создания плейлиста
        FloatingActionButton(
            modifier = Modifier
                .padding(bottom = 32.dp, end = 32.dp)
                .align(Alignment.BottomEnd),
            onClick = onOpenNewPlaylist,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Создать плейлист"
            )
        }
    }
}

@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Обложка плейлиста (слева)
        Box(
            modifier = Modifier.size(56.dp)
        ) {
            // ✅ ИЗМЕНЕНО: используем PlaylistCoverImage вместо AsyncImage
            PlaylistCoverImage(
                imagePath = playlist.coverImagePath,  // ✅ ИЗМЕНЕНО: coverImageUri → coverImagePath
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Информация о плейлисте (центр)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
                    playlist.tracks.isEmpty() -> "Нет треков"
                    playlist.tracks.size == 1 -> "1 трек"
                    playlist.tracks.size in 2..4 -> "${playlist.tracks.size} трека"
                    else -> "${playlist.tracks.size} треков"
                },
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Иконка стрелки (справа)
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "Открыть плейлист",
            modifier = Modifier.size(24.dp),
            tint = Color.Gray.copy(alpha = 0.6f)
        )
    }
}