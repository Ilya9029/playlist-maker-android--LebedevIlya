package com.example.new_project.data.database.dao

import androidx.room.*
import com.example.new_project.data.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для операций с треками в базе данных.
 */
@Dao
interface TrackDao {

    // ========== ОСНОВНЫЕ ОПЕРАЦИИ С ТРЕКАМИ ==========

    /**
     * Вставляет или заменяет трек в базе данных.
     * @param track трек для вставки
     * @return ID вставленного трека
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    /**
     * Вставляет или заменяет несколько треков.
     * @param tracks список треков
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    /**
     * Получает трек по его ID в базе данных.
     * @param id ID трека в базе
     * @return трек или null если не найден
     */
    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): TrackEntity?

    /**
     * Получает трек по внешнему ID (из iTunes API).
     * @param externalId внешний ID трека
     * @return трек или null если не найден
     */
    @Query("SELECT * FROM tracks WHERE external_id = :externalId")
    suspend fun getTrackByExternalId(externalId: String): TrackEntity?

    /**
     * Получает все треки из базы данных.
     * @return Flow со списком всех треков
     */
    @Query("SELECT * FROM tracks ORDER BY created_at DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    /**
     * Получает все избранные треки.
     * @return Flow со списком избранных треков
     */
    @Query("SELECT * FROM tracks WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    /**
     * Ищет треки по названию или исполнителю.
     * @param query поисковый запрос
     * @return список найденных треков
     */
    @Query("SELECT * FROM tracks WHERE track_name LIKE '%' || :query || '%' OR artist_name LIKE '%' || :query || '%'")
    suspend fun searchTracks(query: String): List<TrackEntity>

    // ========== ОПЕРАЦИИ С ИЗБРАННЫМ ==========

    /**
     * Обновляет статус "Избранное" у трека.
     * @param id ID трека
     * @param isFavorite новый статус
     */
    @Query("UPDATE tracks SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    /**
     * Обновляет статус "Избранное" по внешнему ID.
     * @param externalId внешний ID трека
     * @param isFavorite новый статус
     */
    @Query("UPDATE tracks SET is_favorite = :isFavorite WHERE external_id = :externalId")
    suspend fun updateFavoriteStatusByExternalId(externalId: String, isFavorite: Boolean)

    // ========== УДАЛЕНИЕ ==========

    /**
     * Удаляет трек по ID.
     * @param id ID трека
     */
    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    /**
     * Удаляет все треки (использовать с осторожностью!).
     */
    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()
}