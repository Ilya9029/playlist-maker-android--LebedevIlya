package com.example.new_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_project.data.repository.PlaylistsRepository
import com.example.new_project.data.repository.TracksRepository
import com.example.new_project.domain.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана одного конкретного плейлиста
 */
class PlaylistViewModel(
    private val playlistId: Long,
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository // ✅ ДОБАВЛЕНО: для удаления треков
) : ViewModel() {

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val playlistData = playlistsRepository.getPlaylist(playlistId)
                _playlist.value = playlistData

                if (playlistData == null) {
                    _error.value = "Плейлист не найден"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshPlaylist() {
        loadPlaylist()
    }

    // ✅ ИСПРАВЛЕННЫЙ МЕТОД: удаление трека из плейлиста
    suspend fun deleteTrackFromPlaylist(trackId: String): Boolean {
        return try {
            // Используем tracksRepository напрямую
            tracksRepository.deleteTrackFromPlaylist(trackId, playlistId)
            // Обновляем плейлист после удаления
            refreshPlaylist()
            true
        } catch (e: Exception) {
            _error.value = "Ошибка удаления: ${e.message}"
            false
        }
    }
}