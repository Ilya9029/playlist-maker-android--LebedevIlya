package com.example.new_project.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_project.data.preferences.SearchHistoryPreferences
import com.example.new_project.domain.TracksRepository
import com.example.new_project.domain.SearchState
import com.example.new_project.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: TracksRepository,
    private val searchHistoryPreferences: SearchHistoryPreferences
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    // StateFlow для истории поиска (теперь из DataStore)
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    init {
        loadSearchHistory()
    }

    fun loadTrackById(trackId: String) {
        viewModelScope.launch {
            try {
                Log.d("SearchViewModel", "Loading track by id: $trackId")
                val track = repository.getTrackById(trackId)

                if (track != null) {
                    _currentTrack.value = track
                    Log.d("SearchViewModel", "Track loaded: ${track.trackName}, isFavorite: ${track.isFavorite}")
                } else {
                    val cachedTrack = findTrackInCache(trackId)
                    _currentTrack.value = cachedTrack

                    if (cachedTrack == null) {
                        Log.w("SearchViewModel", "Track $trackId not found")
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error loading track by id: $trackId", e)
                _currentTrack.value = null
            }
        }
    }

    fun setCurrentTrack(track: Track) {
        Log.d("SearchViewModel", "Setting current track: ${track.trackName}, isFavorite: ${track.isFavorite}")
        _currentTrack.value = track

        val currentState = _searchScreenState.value
        if (currentState is SearchState.Success) {
            val updatedTracks = currentState.tracks.map {
                if (it.id == track.id) track else it
            }
            _searchScreenState.value = SearchState.Success(updatedTracks)
        }
    }

    private fun findTrackInCache(trackId: String): Track? {
        return when (val state = _searchScreenState.value) {
            is SearchState.Success -> state.tracks.firstOrNull { it.id == trackId }
            else -> null
        }
    }

    fun search(expression: String) {
        if (expression.isBlank()) {
            _searchScreenState.value = SearchState.Initial
            return
        }

        viewModelScope.launch {
            try {
                // Сохраняем запрос в историю
                searchHistoryPreferences.addEntry(expression)

                _searchScreenState.value = SearchState.Searching
                val result = repository.searchTracks(expression)

                if (result.isNotEmpty()) {
                    _currentTrack.value = result.firstOrNull()
                    _searchScreenState.value = SearchState.Success(result)
                } else {
                    Log.d("SearchViewModel", "Empty result for query: '$expression'")
                    _searchScreenState.value = SearchState.Fail("Ничего не найдено для запроса \"$expression\"")
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Search error:", e)
                _searchScreenState.value = SearchState.Fail("Ошибка поиска: ${e.message}")
            }
        }
    }

    // ✅ НОВЫЙ МЕТОД: поиск из истории (просто вызывает обычный search)
    fun searchFromHistory(query: String) {
        // Устанавливаем состояние и запускаем поиск
        _searchScreenState.value = SearchState.Searching
        search(query)
    }

    fun clearSearch() {
        _searchScreenState.value = SearchState.Initial
        _currentTrack.value = null
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            // Подписываемся на Flow из DataStore
            searchHistoryPreferences.searchHistory.collectLatest { history ->
                _searchHistory.value = history
                Log.d("SearchViewModel", "History loaded: $history")
            }
        }
    }

    suspend fun clearSearchHistory() {
        searchHistoryPreferences.clearHistory()
    }
}