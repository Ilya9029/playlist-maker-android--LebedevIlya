package com.example.new_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_project.creator.Creator
import com.example.new_project.data.repository.PlaylistsRepository
import com.example.new_project.domain.Playlist
import kotlinx.coroutines.flow.Flow

/**
 * ViewModel для экрана одного конкретного плейлиста
 * (не путать с PlaylistsViewModel для списка плейлистов)
 */
class PlaylistViewModel(
    // Принимаем ID плейлиста как параметр
    private val playlistId: Long
) : ViewModel() {

    // Получаем репозиторий через Creator
    private val playlistsRepository: PlaylistsRepository =
        Creator.getPlaylistsRepository(viewModelScope)

    /**
     * Flow с данными конкретного плейлиста
     * Возвращает Playlist? (может быть null если плейлист не найден)
     */
    val playlist: Flow<Playlist?> = playlistsRepository.getPlaylist(playlistId)
}