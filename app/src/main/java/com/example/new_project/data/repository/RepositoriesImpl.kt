package com.example.new_project.data.repository

import com.example.new_project.data.TracksSearchRequest
import com.example.new_project.data.database.DatabaseMock
import com.example.new_project.data.dto.TracksSearchResponse
import com.example.new_project.domain.NetworkClient
import com.example.new_project.domain.Playlist
import com.example.new_project.domain.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class PlaylistsRepositoryImpl(
    private val scope: CoroutineScope
) : PlaylistsRepository {
    private val database = DatabaseMock(scope = scope)

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return database.getPlaylist(playlistId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return database.getAllPlaylists()
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        database.addNewPlaylist(name = name, description = description)
    }

    override suspend fun deletePlaylistById(id: Long) {
        database.deletePlaylistById(playlistId = id)
    }
}

// TracksRepositoryImpl.kt - ИЗМЕНИТЕ конструктор и метод searchTracks
class TracksRepositoryImpl(
private val scope: CoroutineScope,
private val networkClient: NetworkClient
) : TracksRepository {
    private val database = DatabaseMock(scope = scope)

    override suspend fun searchTracks(expression: String): List<Track> {
        // Всегда ищем в сети, даже при пустом запросе
        return searchInNetwork(expression)
    }

    private suspend fun searchInNetwork(expression: String): List<Track> {
        android.util.Log.d("Search", "Searching in iTunes API: '$expression'")
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))

            // ✅ Проверяем, что это TracksSearchResponse и есть результаты
            if (response is TracksSearchResponse) {
                val tracks = response.results.map { it.toTrack() }
                android.util.Log.d("Search", "✅ Found ${tracks.size} tracks from API")

                // Кэшируем найденные треки
                tracks.forEach { database.insertTrack(it) }
                tracks
            } else {
                android.util.Log.w("Search", "⚠️ Unexpected response type")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("Search", "🌐 Network error: ${e.message}", e)
            emptyList()
        }
    }


    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        database.insertTrack(track)
        database.addTrackToPlaylist(track.id, playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        database.removeTrackFromPlaylist(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val updatedTrack = track.copy(isFavorite = isFavorite)
        database.insertTrack(updatedTrack)
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }

    // КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: ДОБАВЛЕН ОТСУТСТВУЮЩИЙ МЕТОД
    override suspend fun getTrackById(trackId: String): Track? {
        return database.getTrackById(trackId)
    }
}