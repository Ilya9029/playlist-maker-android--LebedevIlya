package com.example.new_project.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.new_project.domain.SearchState
import com.example.new_project.domain.Track

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    var text by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Введите название трека или исполнителя") },
            leadingIcon = {
                Icon(
                    modifier = Modifier.clickable { viewModel.search(text) },
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        when (screenState) {
            is SearchState.Initial -> {
                Text(
                    text = "Введи запрос и нажми на лупу",
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
            is SearchState.Searching -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SearchState.Success -> {
                val tracks = (screenState as SearchState.Success).list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    items(tracks.size) { index ->
                        val track = tracks[index]
                        TrackListItem(
                            track = track,
                            onClick = { onTrackClick(track) }
                        )
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
            is SearchState.Fail -> {
                val error = (screenState as SearchState.Fail).error
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    }
}
