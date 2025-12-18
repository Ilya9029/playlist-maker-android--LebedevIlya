package com.example.new_project.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_project.creator.Creator
import com.example.new_project.data.repository.TracksRepository
import com.example.new_project.domain.SearchState
import com.example.new_project.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    // ИСПРАВЛЕНО: передаем viewModelScope
    private val repository: TracksRepository = Creator.getTracksRepository(viewModelScope)

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    fun loadTrackById(trackId: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("SearchViewModel", "Loading track by id: $trackId")

                // Пробуем загрузить из репозитория
                val track = repository.getTrackById(trackId)

                if (track != null) {
                    _currentTrack.value = track
                    android.util.Log.d("SearchViewModel", "Track found: ${track.trackName}")
                } else {
                    // Если не нашли в репозитории, ищем в кеше
                    val cachedTrack = findTrackInCache(trackId)
                    _currentTrack.value = cachedTrack

                    if (cachedTrack == null) {
                        android.util.Log.w("SearchViewModel", "Track $trackId not found")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SearchViewModel", "Error loading track by id: $trackId", e)
                _currentTrack.value = null
            }
        }
    }

    private fun findTrackInCache(trackId: String): Track? {
        return when (val state = _searchScreenState.value) {
            is SearchState.Success -> state.tracks.firstOrNull { it.id == trackId }
            else -> null
        }
    }

    fun setCurrentTrack(track: Track) {
        android.util.Log.d("SearchViewModel", "Setting current track: ${track.id}")
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
                    _currentTrack.value = result.firstOrNull() // можно установить первый трек
                    _searchScreenState.value = SearchState.Success(result)
                } else {
                    android.util.Log.d("SearchViewModel", "Empty result for query: '$expression'")
                    _searchScreenState.value = SearchState.Fail("Ничего не найдено для запроса \"$expression\"")
                }
            } catch (e: Exception) {
                android.util.Log.e("SearchViewModel", "Search error:", e)
                _searchScreenState.value = SearchState.Fail("Ошибка поиска: ${e.message}")
            }
        }
    }
}