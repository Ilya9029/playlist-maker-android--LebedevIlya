package com.example.new_project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.new_project.ui.search.SearchViewModel
import com.example.new_project.viewmodel.PlaylistsViewModel
import com.example.new_project.data.model.Playlist
import com.example.new_project.data.model.Track as DbTrack
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    onBack: () -> Unit,
    searchViewModel: SearchViewModel,
    playlistsViewModel: PlaylistsViewModel
) {
    val trackState by searchViewModel.currentTrack.collectAsState()
    val playlists by playlistsViewModel.playlists.collectAsState(initial = emptyList())
    val favorites by playlistsViewModel.favoriteList.collectAsState(initial = emptyList())

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF2962FF)
    ) {
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
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "Track details",
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
                if (trackState == null) {
                    Text(
                        "Трек не выбран",
                        fontSize = 16.sp
                    )
                    return@Column
                }

                val track = trackState!!

                val isFavorite = favorites.any {
                    it.trackName == track.trackName && it.artistName == track.artistName
                }

                Text(
                    text = track.trackName,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.artistName,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Длительность: ${track.trackTime}",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = {
                        val dbTrack = DbTrack(
                            id = 0L,
                            trackName = track.trackName,
                            artistName = track.artistName,
                            trackTimeMillis = 0L,
                            favorite = !isFavorite,
                            playlistId = 0L
                        )
                        scope.launch {
                            playlistsViewModel.toggleFavorite(dbTrack, !isFavorite)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite"
                        )
                    }

                    IconButton(onClick = {
                        showBottomSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.QueueMusic,
                            contentDescription = "Add to playlist"
                        )
                    }
                }
            }

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
                    Text(
                        text = "Выбери плейлист",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    if (playlists.isEmpty()) {
                        Text(
                            text = "Плейлистов пока нет",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        ) {
                            items(playlists) { playlist ->
                                PlaylistRow(
                                    playlist = playlist,
                                    onClick = {
                                        val track = trackState ?: return@PlaylistRow
                                        val dbTrack = DbTrack(
                                            id = 0L,
                                            trackName = track.trackName,
                                            artistName = track.artistName,
                                            trackTimeMillis = 0L,
                                            favorite = favorites.any {
                                                it.trackName == track.trackName &&
                                                        it.artistName == track.artistName
                                            },
                                            playlistId = playlist.id
                                        )
                                        scope.launch {
                                            playlistsViewModel.insertTrackToPlaylist(
                                                dbTrack,
                                                playlist.id
                                            )
                                            sheetState.hide()
                                            showBottomSheet = false
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = playlist.name, fontSize = 16.sp)
        Text(
            text = "${playlist.tracks.size} треков",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
