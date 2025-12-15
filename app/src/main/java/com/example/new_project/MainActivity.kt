package com.example.new_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistMakerApp()
        }
    }
}

@Composable
fun PlaylistMakerApp() {
    MaterialTheme {
        // ВЕСЬ ЭКРАН СИНИЙ
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF2962FF)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // СИНИЙ ЗАГОЛОВОК НА ФОНЕ
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

                // Отступ вниз, чтобы белая карточка была ниже
                Spacer(modifier = Modifier.height(24.dp))

                // БЕЛАЯ КАРТОЧКА С МЕНЮ
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
                    MenuItem("Поиск", topOffset = 8.dp)
                    MenuItem("Плейлисты")
                    MenuItem("Избранное")
                    MenuItem("Настройки")
                }
            }
        }
    }
}

@Composable
fun MenuItem(title: String, topOffset: Dp = 0.dp) {
    val context = LocalContext.current

    val leadingIcon = when (title) {
        "Поиск" -> Icons.Filled.Search
        "Плейлисты" -> Icons.Filled.QueueMusic
        "Избранное" -> Icons.Filled.FavoriteBorder
        "Настройки" -> Icons.Filled.Settings
        else -> Icons.Filled.Search
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topOffset)
            .clickable {
                when (title) {
                    "Поиск" -> {
                        context.startActivity(
                            Intent(context, SearchActivity::class.java)
                        )
                    }
                    "Настройки" -> {
                        context.startActivity(
                            Intent(context, SettingsActivity::class.java)
                        )
                    }
                    // Плейлисты и Избранное пока без переходов
                }
            }
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Отступ слева для иконки + текста
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

        // Расстояние до стрелки
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.padding(end = 24.dp)
        )
    }
}
