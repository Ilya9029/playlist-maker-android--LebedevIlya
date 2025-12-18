package com.example.new_project.data.dto

import com.example.new_project.domain.Track
import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("trackId")
    val trackId: Long? = null,

    @SerializedName("trackName")
    val trackName: String,

    @SerializedName("artistName")
    val artistName: String,

    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long,

    @SerializedName("artworkUrl100")
    val artworkUrl: String?,

    @SerializedName("previewUrl")
    val previewUrl: String?,

    @SerializedName("collectionName")
    val albumName: String?
) {
    /**
     * Конвертирует DTO (данные из API) в доменную модель Track
     */
    fun toTrack(): Track {
        return Track(
            // Генерируем уникальный ID на основе данных
            id = generateTrackId(),
            trackName = trackName,
            artistName = artistName,
            // Конвертируем миллисекунды в читаемый формат "MM:SS"
            trackTime = formatMillisToString(trackTimeMillis),
            albumName = albumName,
            artworkUrl = artworkUrl,
            // По умолчанию трек не в избранном
            isFavorite = false
        )
    }

    /**
     * Генерирует уникальный идентификатор трека.
     * Приоритет: trackId из API → комбинация полей
     */
    private fun generateTrackId(): String {
        return trackId?.toString() ?: createFallbackId()
    }

    /**
     * Создает ID на основе данных трека (если нет trackId из API)
     */
    private fun createFallbackId(): String {
        // Хэшируем для уникальности
        val hash = "${trackName}_${artistName}_${trackTimeMillis}".hashCode()
        return "generated_${hash}"
    }

    /**
     * Форматирует миллисекунды в строку MM:SS
     * Пример: 187000 → "3:07"
     */
    private fun formatMillisToString(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}