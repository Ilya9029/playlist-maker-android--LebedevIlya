package com.example.new_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.new_project.creator.Creator
import com.example.new_project.data.repository.PlaylistsRepository
import com.example.new_project.data.repository.TracksRepository
import com.example.new_project.domain.Playlist
import com.example.new_project.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistsViewModel : ViewModel() {
    private val playlistsRepository: PlaylistsRepository =
        Creator.getPlaylistsRepository(viewModelScope)

    private val tracksRepository: TracksRepository =
        Creator.getTracksRepository(viewModelScope)

    // ✅ ИСПРАВЛЕНО: убрано накопление, просто передаём поток
    val playlists: Flow<List<Playlist>> = playlistsRepository.getAllPlaylists()

    val favoriteList: Flow<List<Track>> = tracksRepository.getFavoriteTracks()

    fun createNewPlayList(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(namePlaylist, description)
        }
    }

    suspend fun toggleFavorite(track: Track) {
        tracksRepository.updateTrackFavoriteStatus(track, !track.isFavorite)
    }

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksRepository.insertTrackToPlaylist(track, playlistId)
    }

    suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksRepository.deleteTrackFromPlaylist(track)
    }

    suspend fun deletePlaylistById(id: Long) {
        tracksRepository.deleteTracksByPlaylistId(id)
        playlistsRepository.deletePlaylistById(id)
    }
}