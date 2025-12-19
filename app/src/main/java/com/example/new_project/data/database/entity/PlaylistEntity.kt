package com.example.new_project.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность для хранения плейлистов в базе данных Room.
 */
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "cover_image_path")  // ✅ ИЗМЕНЕНО: путь к файлу вместо URI
    val coverImagePath: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Конвертирует PlaylistEntity в доменную модель Playlist
     * (список треков будет добавляться отдельно через связь)
     */
    fun toPlaylist(tracks: List<com.example.new_project.domain.Track> = emptyList()): com.example.new_project.domain.Playlist {
        return com.example.new_project.domain.Playlist(
            id = id,
            name = name,
            description = description,
            coverImagePath = coverImagePath,  // ✅ ИЗМЕНЕНО: передаем путь к файлу
            tracks = tracks
        )
    }

    companion object {
        /**
         * Создает PlaylistEntity из доменной модели Playlist
         */
        fun fromPlaylist(playlist: com.example.new_project.domain.Playlist): PlaylistEntity {
            return PlaylistEntity(
                id = playlist.id.takeIf { it > 0 } ?: 0,  // Сохраняем существующий ID или 0 для нового
                name = playlist.name,
                description = playlist.description,
                coverImagePath = playlist.coverImagePath  // ✅ ИЗМЕНЕНО: сохраняем путь к файлу
            )
        }
    }
}