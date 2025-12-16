package com.example.new_project.domain

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
}
