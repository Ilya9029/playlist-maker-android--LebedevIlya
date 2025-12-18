package com.example.new_project.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["external_id"], unique = true)
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "external_id")
    val externalId: String?,

    @ColumnInfo(name = "track_name")
    val trackName: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "track_time")
    val trackTime: String,

    @ColumnInfo(name = "album_name")
    val albumName: String?,

    @ColumnInfo(name = "artwork_url")
    val artworkUrl: String?,

    @ColumnInfo(name = "preview_url")
    val previewUrl: String? = null,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toTrack(): com.example.new_project.domain.Track {
        return com.example.new_project.domain.Track(
            // ✅ ВАЖНО: если externalId есть - используем его, иначе local_ + id
            id = externalId ?: "local_$id",
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            albumName = albumName,
            artworkUrl = artworkUrl,
            // ✅ ВАЖНО: берем isFavorite ИЗ БАЗЫ ДАННЫХ
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromTrack(track: com.example.new_project.domain.Track): TrackEntity {
            // Определяем externalId: если track.id НЕ начинается с "local_", то это externalId
            val externalId = if (!track.id.startsWith("local_")) {
                track.id
            } else {
                null
            }

            return TrackEntity(
                externalId = externalId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                albumName = track.albumName,
                artworkUrl = track.artworkUrl,
                isFavorite = track.isFavorite
            )
        }
    }
}