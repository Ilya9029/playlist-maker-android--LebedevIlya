package com.example.new_project.domain

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUri: String? = null,  // ✅ НОВОЕ: URI обложки плейлиста
    var tracks: List<Track> = emptyList()
)