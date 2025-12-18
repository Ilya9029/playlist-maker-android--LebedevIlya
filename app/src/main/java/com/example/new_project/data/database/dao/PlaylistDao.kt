package com.example.new_project.data.database.dao

import androidx.room.*
import com.example.new_project.data.database.entity.PlaylistEntity
import com.example.new_project.data.database.entity.PlaylistTrackCrossRef
import com.example.new_project.data.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для операций с плейлистами и связями плейлист-трек.
 */
@Dao
interface PlaylistDao {

    // ========== ОСНОВНЫЕ ОПЕРАЦИИ С ПЛЕЙЛИСТАМИ ==========

    /**
     * Вставляет плейлист в базу данных.
     * @param playlist плейлист для вставки
     * @return ID вставленного плейлиста
     */
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    /**
     * Обновляет существующий плейлист.
     * @param playlist плейлист для обновления
     */
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    /**
     * Получает плейлист по ID.
     * @param id ID плейлиста
     * @return плейлист или null если не найден
     */
    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    /**
     * Получает все плейлисты с количеством треков.
     * @return Flow со списком всех плейлистов
     */
    @Query("SELECT * FROM playlists ORDER BY created_at DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    /**
     * Удаляет плейлист по ID.
     * @param id ID плейлиста
     */
    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylistById(id: Long)

    // ========== ОПЕРАЦИИ СО СВЯЗЯМИ ПЛЕЙЛИСТ-ТРЕК ==========

    /**
     * Добавляет связь между плейлистом и треком.
     * @param crossRef связь плейлист-трек
     */
    @Insert
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    /**
     * Удаляет связь между плейлистом и треком.
     * @param crossRef связь плейлист-трек
     */
    @Delete
    suspend fun removeTrackFromPlaylist(crossRef: PlaylistTrackCrossRef)

    /**
     * Удаляет все связи треков с указанным плейлистом.
     * @param playlistId ID плейлиста
     */
    @Query("DELETE FROM playlist_track_join WHERE playlist_id = :playlistId")
    suspend fun removeAllTracksFromPlaylist(playlistId: Long)

    /**
     * Проверяет, есть ли трек в плейлисте.
     * @param playlistId ID плейлиста
     * @param trackId ID трека в БД
     * @return true если связь существует, false если нет
     */
    @Query("SELECT COUNT(*) > 0 FROM playlist_track_join WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    // ========== СЛОЖНЫЕ ЗАПРОСЫ С JOIN ==========

    /**
     * Получает плейлист вместе со всеми его треками.
     * Используется для загрузки полных данных плейлиста.
     * @param playlistId ID плейлиста
     * @return плейлист со списком треков (через Relation в AppDatabase)
     */
    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks?

    /**
     * Получает все плейлисты вместе с их треками.
     * @return список плейлистов с треками
     */
    @Transaction
    @Query("SELECT * FROM playlists ORDER BY created_at DESC")
    fun getAllPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>

    /**
     * Получает все треки указанного плейлиста.
     * @param playlistId ID плейлиста
     * @return список треков в плейлисте
     */
    @Query("""
        SELECT tracks.* FROM tracks
        INNER JOIN playlist_track_join ON tracks.id = playlist_track_join.track_id
        WHERE playlist_track_join.playlist_id = :playlistId
        ORDER BY tracks.created_at DESC
    """)
    suspend fun getTracksForPlaylist(playlistId: Long): List<TrackEntity>
}

/**
 * Класс-обертка для возврата плейлиста со списком треков.
 * Room автоматически заполнит список треков через @Relation.
 */
data class PlaylistWithTracks(
    @Embedded
    val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistTrackCrossRef::class,
            parentColumn = "playlist_id",
            entityColumn = "track_id"
        )
    )
    val tracks: List<TrackEntity>
)