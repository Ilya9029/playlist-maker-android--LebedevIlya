package com.example.new_project.data.model

data class Track(
    val id: Long = 0,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long = 0,
    var favorite: Boolean = false,
    var playlistId: Long = 0
)

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    var tracks: List<Track> = emptyList()
)
