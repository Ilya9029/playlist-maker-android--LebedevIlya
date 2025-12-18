package com.example.new_project.data.database

import com.example.new_project.domain.Playlist
import com.example.new_project.domain.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DatabaseMock(
    private val scope: CoroutineScope,
) {
    // Основные хранилища
    private val playlists = mutableListOf<Playlist>()
    private val tracks = mutableListOf<Track>()  // Все треки

    // Отдельная мапа для связей трек → плейлист
    // Ключ: track.id (String), Значение: playlist.id (Long)
    private val trackToPlaylistMap = mutableMapOf<String, Long>()

    // Начальные данные для тестирования
    init {
        // Добавим тестовые плейлисты
        playlists.add(Playlist(id = 1, name = "Избранное", description = "Любимые треки", tracks = emptyList()))
        playlists.add(Playlist(id = 2, name = "Рок", description = "Рок музыка", tracks = emptyList()))

        // Добавим тестовые треки
        tracks.addAll(
            listOf(
                Track(
                    id = "track_1",
                    trackName = "Bohemian Rhapsody",
                    artistName = "Queen",
                    trackTime = "5:55",
                    isFavorite = true
                ),
                Track(
                    id = "track_2",
                    trackName = "Smells Like Teen Spirit",
                    artistName = "Nirvana",
                    trackTime = "5:01",
                    isFavorite = false
                ),
                Track(
                    id = "track_3",
                    trackName = "Hotel California",
                    artistName = "Eagles",
                    trackTime = "6:30",
                    isFavorite = true
                )
            )
        )

        // Создадим связи для теста
        trackToPlaylistMap["track_1"] = 1  // Queen → Избранное
        trackToPlaylistMap["track_2"] = 2  // Nirvana → Рок
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        delay(500)  // Имитация задержки сети/БД

        val result = mutableListOf<Playlist>()

        playlists.forEach { playlist ->
            // Находим ID треков этого плейлиста
            val trackIdsInPlaylist = trackToPlaylistMap
                .filter { (_, playlistId) -> playlistId == playlist.id }
                .keys
                .toSet()

            // Находим сами треки
            val playlistTracks = tracks.filter { it.id in trackIdsInPlaylist }

            // Создаем плейлист с треками
            result.add(playlist.copy(tracks = playlistTracks))
        }

        emit(result.toList())
    }

    fun getPlaylist(id: Long): Flow<Playlist?> = flow {
        delay(300)

        // Находим плейлист
        val playlist = playlists.find { it.id == id }

        // Если нашли, добавляем треки
        if (playlist != null) {
            val trackIdsInPlaylist = trackToPlaylistMap
                .filter { (_, playlistId) -> playlistId == playlist.id }
                .keys
                .toSet()

            val playlistTracks = tracks.filter { it.id in trackIdsInPlaylist }
            emit(playlist.copy(tracks = playlistTracks))
        } else {
            emit(null)
        }
    }

    fun addNewPlaylist(name: String, description: String) {
        val newId = (playlists.maxOfOrNull { it.id } ?: 0) + 1
        playlists.add(
            Playlist(
                id = newId,
                name = name,
                description = description,
                tracks = emptyList()
            )
        )
    }

    fun deletePlaylistById(playlistId: Long) {
        // Удаляем плейлист
        playlists.removeIf { it.id == playlistId }

        // Удаляем все связи с этим плейлистом
        trackToPlaylistMap.entries.removeIf { (_, pid) -> pid == playlistId }
    }

    fun insertTrack(track: Track) {
        // Удаляем старую версию трека если есть
        tracks.removeIf { it.id == track.id }
        // Добавляем новую
        tracks.add(track)
    }

    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        delay(300)
        // Используем isFavorite
        val favorites = tracks.filter { it.isFavorite }
        emit(favorites)
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        // Находим ID треков этого плейлиста
        val trackIdsToRemove = trackToPlaylistMap
            .filter { (_, pid) -> pid == playlistId }
            .keys
            .toList()

        // Удаляем связи
        trackIdsToRemove.forEach { trackId ->
            trackToPlaylistMap.remove(trackId)
        }
    }

    // НОВЫЙ МЕТОД: добавить связь трек-плейлист
    fun addTrackToPlaylist(trackId: String, playlistId: Long) {
        trackToPlaylistMap[trackId] = playlistId
    }

    // НОВЫЙ МЕТОД: удалить связь трек-плейлист
    fun removeTrackFromPlaylist(trackId: String) {
        trackToPlaylistMap.remove(trackId)
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        delay(200)
        emit(tracks.find {
            it.trackName.equals(track.trackName, ignoreCase = true) &&
                    it.artistName.equals(track.artistName, ignoreCase = true)
        })
    }

    fun searchTracks(expression: String): List<Track> {
        return if (expression.isBlank()) {
            tracks.take(10)  // Возвращаем первые 10 треков если запрос пустой
        } else {
            tracks.filter {
                it.trackName.contains(expression, ignoreCase = true) ||
                        it.artistName.contains(expression, ignoreCase = true)
            }
        }
    }

    // НОВЫЙ МЕТОД: найти трек по ID
    fun getTrackById(trackId: String): Track? {
        return tracks.find { it.id == trackId }
    }
}