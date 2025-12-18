package com.example.new_project.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onTrackClick: (com.example.new_project.domain.Track) -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFieldFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color(0xFF2962FF),
        backgroundColor = Color(0xFF2962FF).copy(alpha = 0.4f)
    )

    // Функция для выполнения поиска
    fun performSearch() {
        if (searchQuery.isNotBlank()) {
            viewModel.search(searchQuery)
            keyboardController?.hide()
            isSearchFieldFocused = false
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        // Поле поиска с выпадающей историей
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Кастомное текстовое поле
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isSearchFieldFocused = focusState.isFocused
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        bringIntoViewRequester.bringIntoView()
                                    }
                                }
                            }
                            .bringIntoViewRequester(bringIntoViewRequester),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(Color(0xFF2962FF)),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                performSearch()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Кнопка поиска (лупа) - всегда видимая
                                    IconButton(
                                        onClick = { performSearch() },
                                        modifier = Modifier.size(24.dp),
                                        enabled = searchQuery.isNotBlank()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "Поиск",
                                            tint = if (searchQuery.isNotBlank()) Color(0xFF2962FF) else Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "Введите название трека или исполнителя",
                                                fontSize = 16.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        innerTextField()
                                    }

                                    // Кнопка очистки
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                searchQuery = ""
                                                keyboardController?.hide()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Очистить",
                                                modifier = Modifier.size(18.dp),
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                // Выпадающая история поиска
                if (isSearchFieldFocused && searchHistory.isNotEmpty() && searchQuery.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            // Заголовок истории
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "История поиска",
                                    fontSize = 14.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                    color = Color.Gray
                                )
                            }

                            // Список истории
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                            ) {
                                items(searchHistory) { historyItem ->
                                    SearchHistoryDropdownItem(
                                        query = historyItem,
                                        onClick = {
                                            searchQuery = historyItem
                                            viewModel.searchFromHistory(historyItem)
                                            isSearchFieldFocused = false
                                            keyboardController?.hide()
                                        }
                                    )
                                    Divider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = Color.Gray.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Состояния поиска
        when (screenState) {
            is com.example.new_project.domain.SearchState.Initial -> {
                if (!isSearchFieldFocused) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Поиск",
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = "Начните поиск",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Введите название трека или исполнителя",
                                fontSize = 14.sp,
                                color = Color.Gray.copy(alpha = 0.7f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            is com.example.new_project.domain.SearchState.Searching -> {
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
                        CircularProgressIndicator(
                            color = Color(0xFF2962FF)
                        )
                        Text(
                            text = "Ищем...",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            is com.example.new_project.domain.SearchState.Success -> {
                val tracks = (screenState as com.example.new_project.domain.SearchState.Success).tracks

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
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Ничего не найдено",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = "Ничего не найдено",
                                fontSize = 18.sp,
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
                            .padding(top = 8.dp)
                    ) {
                        items(
                            items = tracks,
                            key = { it.id }
                        ) { track ->
                            TrackListItem(
                                track = track,
                                onClick = { onTrackClick(track) }
                            )
                            Divider(
                                thickness = 0.5.dp,
                                color = Color.Gray.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }

            is com.example.new_project.domain.SearchState.Fail -> {
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
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Ошибка",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "Ошибка поиска",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = (screenState as com.example.new_project.domain.SearchState.Fail).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Button(
                            onClick = { performSearch() },
                            enabled = searchQuery.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2962FF)
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHistoryDropdownItem(
    query: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = "История поиска",
            modifier = Modifier
                .size(20.dp)
                .padding(end = 12.dp),
            tint = Color.Gray
        )
        Text(
            text = query,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}