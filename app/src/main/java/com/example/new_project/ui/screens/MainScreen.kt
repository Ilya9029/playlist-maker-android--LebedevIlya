package com.example.new_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onOpenSongs: () -> Unit,
    onOpenPlaylists: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit  // ✅ НОВОЕ: параметр для настроек
) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF2962FF)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = "Playlist maker",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp, top = 10.dp)
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
                ) {
                    MenuItem("Songs", topOffset = 8.dp, onClick = onOpenSongs)
                    MenuItem("Playlists", onClick = onOpenPlaylists)
                    MenuItem("Favorites", onClick = onOpenFavorites)
                    MenuItem("Settings", onClick = onOpenSettings)  // ✅ НОВОЕ: пункт настроек
                }
            }
        }
    }
}

@Composable
fun MenuItem(
    title: String,
    topOffset: Dp = 0.dp,
    onClick: () -> Unit = {}
) {
    val leadingIcon = when (title) {
        "Songs" -> Icons.Filled.Search
        "Playlists" -> Icons.Filled.QueueMusic
        "Favorites" -> Icons.Filled.FavoriteBorder
        "Settings" -> Icons.Filled.Settings  // ✅ НОВОЕ: иконка для настроек
        else -> Icons.Filled.Search
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topOffset)
            .clickable { onClick() }
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(24.dp))

        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            modifier = Modifier.padding(end = 12.dp)
        )

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.padding(end = 24.dp)
        )
    }
}