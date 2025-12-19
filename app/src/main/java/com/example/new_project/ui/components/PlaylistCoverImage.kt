package com.example.new_project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.new_project.R
import com.example.new_project.data.utils.loadBitmapFromPath
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlaylistCoverImage(
    imagePath: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier
    ) {
        if (!imagePath.isNullOrEmpty()) {
            val bitmap = loadBitmapFromPath(imagePath)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Обложка плейлиста",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            } else {
                // Если файл не найден или ошибка загрузки
                PlaceholderCover()
            }
        } else {
            // Если путь пустой
            PlaceholderCover()
        }
    }
}

@Composable
private fun PlaceholderCover() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = "Нет обложки",
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
    }
}