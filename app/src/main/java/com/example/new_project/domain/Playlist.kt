package com.example.new_project.domain

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImagePath: String? = null,  // ✅ ИЗМЕНЕНО: путь к файлу вместо URI
    var tracks: List<Track> = emptyList()
)