package com.example.new_project.data.database.entity

import androidx.room.Entity

/**
 * Связующая таблица для отношения многие-ко-многим между плейлистами и треками.
 * Каждая запись связывает один плейлист с одним треком.
 */
@Entity(
    tableName = "playlist_track_join",
    primaryKeys = ["playlist_id", "track_id"]
)
data class PlaylistTrackCrossRef(
    val playlist_id: Long,
    val track_id: Long
)