package com.example.new_project.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.new_project.domain.SearchState

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onTrackClick: (com.example.new_project.domain.Track) -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Сохраняем последний запрос для кнопки "Обновить"
    var lastFailedQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        // Поле поиска с кнопками очистки и поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Введите название трека или исполнителя") },
            trailingIcon = {
                // Показываем крестик для очистки, если есть текст
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Очистить поиск"
                        )
                    }
                } else {
                    // Или кнопку поиска, если текст есть
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                viewModel.search(searchQuery)
                            }
                        },
                        enabled = searchQuery.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Найти"
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.search(searchQuery)
                        keyboardController?.hide()
                    }
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            )
        )

        // Состояния поиска
        when (screenState) {
            is SearchState.Initial -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Введите запрос и нажмите поиск",
                        color = Color.Gray
                    )
                }
            }

            is SearchState.Searching -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SearchState.Success -> {
                val tracks = (screenState as SearchState.Success).tracks

                if (tracks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Ничего не найдено",
                                color = Color.Gray
                            )
                            Text(
                                text = "Попробуйте другой запрос",
                                fontSize = 14.sp,
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        items(
                            items = tracks,
                            key = { it.id }
                        ) { track ->
                            TrackListItem(
                                track = track,
                                onClick = { onTrackClick(track) }
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }

            is SearchState.Fail -> {
                val errorMessage = (screenState as SearchState.Fail).message
                // Сохраняем запрос для кнопки "Обновить"
                LaunchedEffect(screenState) {
                    lastFailedQuery = searchQuery
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Иконка ошибки (можно заменить на Image)
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Ошибка",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "Ошибка сервера",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        // Кнопка "Обновить"
                        Button(
                            onClick = {
                                if (lastFailedQuery.isNotBlank()) {
                                    viewModel.search(lastFailedQuery)
                                }
                            },
                            enabled = lastFailedQuery.isNotBlank()
                        ) {
                            Text("Обновить")
                        }
                    }
                }
            }
        }
    }
}