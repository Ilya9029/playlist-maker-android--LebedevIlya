package com.example.new_project.data.repository

import android.util.Log
import com.example.new_project.data.TracksSearchRequest
import com.example.new_project.data.database.dao.PlaylistDao
import com.example.new_project.data.database.dao.TrackDao
import com.example.new_project.data.database.entity.PlaylistTrackCrossRef
import com.example.new_project.data.database.entity.TrackEntity
import com.example.new_project.data.dto.TracksSearchResponse
import com.example.new_project.domain.NetworkClient
import com.example.new_project.domain.Playlist
import com.example.new_project.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao
) : PlaylistsRepository {

    override suspend fun getPlaylist(playlistId: Long): Playlist? {
        val playlistWithTracks = playlistDao.getPlaylistWithTracks(playlistId)

        return playlistWithTracks?.let {
            it.playlist.toPlaylist(
                tracks = it.tracks.map { trackEntity ->
                    trackEntity.toTrack()
                }
            )
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylistsWithTracks()
            .map { playlistsWithTracks ->
                playlistsWithTracks.map { playlistWithTracks ->
                    playlistWithTracks.playlist.toPlaylist(
                        tracks = playlistWithTracks.tracks.map { trackEntity ->
                            trackEntity.toTrack()
                        }
                    )
                }
            }
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        val playlistEntity = com.example.new_project.data.database.entity.PlaylistEntity(
            name = name,
            description = description
        )
        playlistDao.insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.removeAllTracksFromPlaylist(id)
        playlistDao.deletePlaylistById(id)
    }
}

class TracksRepositoryImpl(
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val networkClient: NetworkClient
) : TracksRepository {

    // ========== КЛЮЧЕВОЙ МЕТОД: гарантирует наличие трека в БД ==========
    private suspend fun ensureTrackInDatabase(track: Track): TrackEntity {
        Log.d("TracksRepository", "Ensuring track in DB: ${track.id}")

        // 1. Пробуем найти по externalId (из iTunes API)
        var trackEntity = trackDao.getTrackByExternalId(track.id)

        // 2. Если не нашли, пробуем извлечь чистый ID
        if (trackEntity == null) {
            // Если ID начинается с "local_", удаляем префикс
            val cleanId = if (track.id.startsWith("local_")) {
                track.id.removePrefix("local_")
            } else {
                track.id
            }
            trackEntity = trackDao.getTrackByExternalId(cleanId)
        }

        // 3. Если трека нет в БД вообще - создаем
        if (trackEntity == null) {
            Log.d("TracksRepository", "Track not found, creating new: ${track.trackName}")
            val newEntity = TrackEntity.fromTrack(track)
            val newId = trackDao.insertTrack(newEntity)
            trackEntity = newEntity.copy(id = newId)
            Log.d("TracksRepository", "Created track with ID: $newId")
        } else {
            Log.d("TracksRepository", "Track found in DB with ID: ${trackEntity.id}, isFavorite: ${trackEntity.isFavorite}")
        }

        return trackEntity
    }

    // ========== ВСПОМОГАТЕЛЬНЫЙ МЕТОД: находит трек в БД ==========
    private suspend fun findTrackEntity(track: Track): TrackEntity? {
        var trackEntity = trackDao.getTrackByExternalId(track.id)

        if (trackEntity == null) {
            val cleanId = if (track.id.startsWith("local_")) {
                track.id.removePrefix("local_")
            } else {
                track.id
            }
            trackEntity = trackDao.getTrackByExternalId(cleanId)
        }

        return trackEntity
    }

    // ========== ОСНОВНЫЕ МЕТОДЫ ==========
    override suspend fun searchTracks(expression: String): List<Track> {
        return searchInNetwork(expression)
    }

    private suspend fun searchInNetwork(expression: String): List<Track> {
        Log.d("Search", "Searching in iTunes API: '$expression'")
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))

            if (response is TracksSearchResponse) {
                val tracks = response.results.map { it.toTrack() }
                Log.d("Search", "✅ Found ${tracks.size} tracks from API")

                tracks.forEach { track ->
                    ensureTrackInDatabase(track)
                }

                tracks
            } else {
                Log.w("Search", "⚠️ Unexpected response type")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Search", "🌐 Network error: ${e.message}", e)
            emptyList()
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return trackDao.getAllTracks()
            .map { tracks ->
                tracks.find { entity ->
                    entity.trackName.equals(track.trackName, ignoreCase = true) &&
                            entity.artistName.equals(track.artistName, ignoreCase = true)
                }?.toTrack()
            }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks()
            .map { entities ->
                entities.map { it.toTrack() }
            }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        Log.d("TracksRepository", "Adding track to playlist: ${track.id} -> playlist $playlistId")

        val trackEntity = ensureTrackInDatabase(track)

        Log.d("TracksRepository", "Track entity ID: ${trackEntity.id}, external ID: ${trackEntity.externalId}")

        val crossRef = PlaylistTrackCrossRef(
            playlist_id = playlistId,
            track_id = trackEntity.id
        )

        playlistDao.addTrackToPlaylist(crossRef)
        Log.d("TracksRepository", "Track added to playlist successfully")
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Long) {
        Log.d("TracksRepository", "Deleting track $trackId from playlist $playlistId")

        // 1. Находим трек в БД
        val track = getTrackById(trackId)

        if (track != null) {
            val trackEntity = findTrackEntity(track)

            if (trackEntity != null) {
                // 2. Удаляем связь из промежуточной таблицы
                val crossRef = PlaylistTrackCrossRef(
                    playlist_id = playlistId,
                    track_id = trackEntity.id
                )
                playlistDao.removeTrackFromPlaylist(crossRef)
                Log.d("TracksRepository", "Track removed from playlist successfully")
            } else {
                Log.w("TracksRepository", "Track entity not found for ID: $trackId")
            }
        } else {
            Log.w("TracksRepository", "Track $trackId not found in database")
        }
    }

    // ✅ НОВЫЙ МЕТОД: проверка на наличие трека в плейлисте
    override suspend fun isTrackInPlaylist(track: Track, playlistId: Long): Boolean {
        Log.d("TracksRepository", "Checking if track is in playlist: ${track.id}, playlist: $playlistId")

        // 1. Находим трек в БД
        val trackEntity = findTrackEntity(track)

        if (trackEntity == null) {
            Log.d("TracksRepository", "Track not found in DB")
            return false
        }

        // 2. Проверяем связь в промежуточной таблице
        val isInPlaylist = playlistDao.isTrackInPlaylist(playlistId, trackEntity.id)

        Log.d("TracksRepository", "Track ${track.trackName} in playlist $playlistId: $isInPlaylist")
        return isInPlaylist
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        Log.d("TracksRepository", "=== START updateTrackFavoriteStatus ===")
        Log.d("TracksRepository", "Track: ${track.trackName}, ID: ${track.id}, New isFavorite: $isFavorite")

        // 1. Ищем трек в БД (независимо от текущего isFavorite в track)
        var trackEntity = trackDao.getTrackByExternalId(track.id)

        // 2. Если не нашли, пробуем без local_ префикса
        if (trackEntity == null) {
            val cleanId = if (track.id.startsWith("local_")) {
                track.id.removePrefix("local_")
            } else {
                track.id
            }
            trackEntity = trackDao.getTrackByExternalId(cleanId)
        }

        // 3. Если трек не найден - создаем с правильным isFavorite
        if (trackEntity == null) {
            Log.d("TracksRepository", "Track not found in DB, creating new with isFavorite: $isFavorite")
            val newEntity = TrackEntity.fromTrack(track.copy(isFavorite = isFavorite))
            val newId = trackDao.insertTrack(newEntity)
            trackEntity = newEntity.copy(id = newId)
            Log.d("TracksRepository", "Created new track with ID: $newId")
        } else {
            // 4. Трек найден - обновляем isFavorite
            Log.d("TracksRepository", "Track found in DB. ID: ${trackEntity.id}, Current isFavorite: ${trackEntity.isFavorite}")

            if (trackEntity.isFavorite != isFavorite) {
                trackDao.updateFavoriteStatus(trackEntity.id, isFavorite)
                Log.d("TracksRepository", "Updated isFavorite to: $isFavorite")
            } else {
                Log.d("TracksRepository", "isFavorite already correct, no update needed")
            }
        }

        Log.d("TracksRepository", "=== END updateTrackFavoriteStatus ===")
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        playlistDao.removeAllTracksFromPlaylist(playlistId)
    }

    override suspend fun getTrackById(trackId: String): Track? {
        Log.d("TracksRepository", "Getting track by ID: $trackId")

        var trackEntity = trackDao.getTrackByExternalId(trackId)

        if (trackEntity == null) {
            val cleanId = if (trackId.startsWith("local_")) {
                trackId.removePrefix("local_")
            } else {
                trackId
            }
            trackEntity = trackDao.getTrackByExternalId(cleanId)
        }

        if (trackEntity != null) {
            Log.d("TracksRepository", "Track found: ${trackEntity.trackName}, isFavorite: ${trackEntity.isFavorite}")
        } else {
            Log.w("TracksRepository", "Track not found: $trackId")
        }

        return trackEntity?.toTrack()
    }
}