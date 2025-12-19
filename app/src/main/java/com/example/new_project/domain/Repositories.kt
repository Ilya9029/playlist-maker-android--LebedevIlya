package com.example.new_project.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun getPlaylist(playlistId: Long): Playlist?
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addNewPlaylist(name: String, description: String, coverImagePath: String? = null)  // ✅ ИЗМЕНЕНО: coverImageUri → coverImagePath
    suspend fun deletePlaylistById(id: Long)
}

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
    fun getTrackByNameAndArtist(track: Track): Flow<Track?>
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Long)
    suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean)
    suspend fun deleteTracksByPlaylistId(playlistId: Long)
    suspend fun getTrackById(trackId: String): Track?
    suspend fun isTrackInPlaylist(track: Track, playlistId: Long): Boolean
}