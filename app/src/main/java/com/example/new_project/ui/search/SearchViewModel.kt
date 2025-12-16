package com.example.new_project.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_project.creator.Creator
import com.example.new_project.domain.SearchState
import com.example.new_project.domain.Track
import com.example.new_project.domain.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    // Репозиторий берём через Creator (без параметров в конструкторе ViewModel)
    private val repository: TracksRepository = Creator.getTracksRepository()

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    fun setCurrentTrack(track: Track) {
        _currentTrack.value = track
    }

    fun search(expression: String) {
        if (expression.isBlank()) {
            _searchScreenState.value = SearchState.Initial
            return
        }

        viewModelScope.launch {
            try {
                _searchScreenState.value = SearchState.Searching
                val result = repository.searchTracks(expression)
                if (result.isNotEmpty()) {
                    _searchScreenState.value = SearchState.Success(result)
                } else {
                    _searchScreenState.value = SearchState.Fail("Ничего не найдено")
                }
            } catch (e: Exception) {
                _searchScreenState.value = SearchState.Fail("Ошибка поиска")
            }
        }
    }
}
