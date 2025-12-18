package com.example.new_project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_project.data.repository.PlaylistsRepository
import com.example.new_project.data.repository.TracksRepository
import com.example.new_project.domain.Playlist
import com.example.new_project.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository
) : ViewModel() {

    // ========== СОСТОЯНИЯ ДЛЯ ПЛЕЙЛИСТОВ ==========
    val playlists = playlistsRepository.getAllPlaylists()

    private val _playlistsError = MutableStateFlow<String?>(null)
    val playlistsError: StateFlow<String?> = _playlistsError.asStateFlow()

    // ========== СОСТОЯНИЯ ДЛЯ ИЗБРАННОГО ==========
    val favoriteList = tracksRepository.getFavoriteTracks()

    private val _favoritesError = MutableStateFlow<String?>(null)
    val favoritesError: StateFlow<String?> = _favoritesError.asStateFlow()

    // ========== СОСТОЯНИЯ ДЛЯ СОЗДАНИЯ ПЛЕЙЛИСТА ==========
    private val _isCreatingPlaylist = MutableStateFlow(false)
    val isCreatingPlaylist: StateFlow<Boolean> = _isCreatingPlaylist.asStateFlow()

    private val _createPlaylistError = MutableStateFlow<String?>(null)
    val createPlaylistError: StateFlow<String?> = _createPlaylistError.asStateFlow()

    private val _createPlaylistSuccess = MutableStateFlow(false)
    val createPlaylistSuccess: StateFlow<Boolean> = _createPlaylistSuccess.asStateFlow()

    // ========== ОСНОВНЫЕ МЕТОДЫ ==========

    fun createNewPlayList(namePlaylist: String, description: String) {
        if (namePlaylist.isBlank()) {
            _createPlaylistError.value = "Введите название плейлиста"
            return
        }

        viewModelScope.launch {
            try {
                _isCreatingPlaylist.value = true
                _createPlaylistError.value = null
                _createPlaylistSuccess.value = false

                playlistsRepository.addNewPlaylist(namePlaylist, description)
                _createPlaylistSuccess.value = true
            } catch (e: Exception) {
                _createPlaylistError.value = "Ошибка создания: ${e.message}"
            } finally {
                _isCreatingPlaylist.value = false
            }
        }
    }

    suspend fun toggleFavorite(track: Track) {
        try {
            Log.d("PlaylistsViewModel", "toggleFavorite called: ${track.trackName}, new isFavorite: ${!track.isFavorite}")
            tracksRepository.updateTrackFavoriteStatus(track, !track.isFavorite)
            _favoritesError.value = null
            Log.d("PlaylistsViewModel", "toggleFavorite completed successfully")
        } catch (e: Exception) {
            Log.e("PlaylistsViewModel", "Error in toggleFavorite: ${e.message}", e)
            _favoritesError.value = "Ошибка обновления избранного: ${e.message}"
            throw e
        }
    }

    suspend fun updateTrackFavorite(track: Track, isFavorite: Boolean) {
        try {
            Log.d("PlaylistsViewModel", "updateTrackFavorite called: ${track.trackName}, isFavorite: $isFavorite")
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
            _favoritesError.value = null
            Log.d("PlaylistsViewModel", "updateTrackFavorite completed successfully")
        } catch (e: Exception) {
            Log.e("PlaylistsViewModel", "Error in updateTrackFavorite: ${e.message}", e)
            _favoritesError.value = "Ошибка обновления избранного: ${e.message}"
            throw e
        }
    }

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        try {
            tracksRepository.insertTrackToPlaylist(track, playlistId)
        } catch (e: Exception) {
            throw Exception("Ошибка добавления трека в плейлист: ${e.message}")
        }
    }

    // ✅ НОВЫЙ МЕТОД: проверка и добавление трека с проверкой
    suspend fun addTrackToPlaylistIfNotExists(track: Track, playlistId: Long): AddTrackResult {
        return try {
            // 1. Проверяем, есть ли уже трек в плейлисте
            val alreadyExists = tracksRepository.isTrackInPlaylist(track, playlistId)

            if (alreadyExists) {
                Log.d("PlaylistsViewModel", "Track ${track.trackName} already in playlist $playlistId")
                return AddTrackResult.AlreadyExists
            }

            // 2. Добавляем, если нет
            tracksRepository.insertTrackToPlaylist(track, playlistId)
            Log.d("PlaylistsViewModel", "Track ${track.trackName} added to playlist $playlistId")
            AddTrackResult.Success
        } catch (e: Exception) {
            Log.e("PlaylistsViewModel", "Error adding track to playlist: ${e.message}", e)
            AddTrackResult.Error(e.message ?: "Неизвестная ошибка")
        }
    }

    // ✅ ВСПОМОГАТЕЛЬНЫЙ КЛАСС для результата
    sealed class AddTrackResult {
        object Success : AddTrackResult()
        object AlreadyExists : AddTrackResult()
        data class Error(val message: String) : AddTrackResult()
    }

    suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Long) {
        try {
            tracksRepository.deleteTrackFromPlaylist(trackId, playlistId)
        } catch (e: Exception) {
            throw Exception("Ошибка удаления трека: ${e.message}")
        }
    }

    suspend fun deletePlaylistById(id: Long) {
        try {
            tracksRepository.deleteTracksByPlaylistId(id)
            playlistsRepository.deletePlaylistById(id)
        } catch (e: Exception) {
            throw Exception("Ошибка удаления плейлиста: ${e.message}")
        }
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    fun clearCreatePlaylistError() {
        _createPlaylistError.value = null
    }

    fun clearCreatePlaylistSuccess() {
        _createPlaylistSuccess.value = false
    }

    fun clearPlaylistsError() {
        _playlistsError.value = null
    }

    fun clearFavoritesError() {
        _favoritesError.value = null
    }
}