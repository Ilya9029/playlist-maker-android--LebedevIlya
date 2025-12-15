package com.example.new_project.domain

import com.example.new_project.data.Track

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
}
