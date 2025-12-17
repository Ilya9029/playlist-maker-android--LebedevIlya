package com.example.new_project.data

import com.example.new_project.data.dto.TracksSearchResponse
import com.example.new_project.domain.*

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            200 -> {
                return if (response is TracksSearchResponse) {
                    response.results.map { trackDto ->
                        val totalSeconds = trackDto.trackTimeMillis / 1000
                        val minutes = totalSeconds / 60
                        val seconds = totalSeconds % 60
                        val formattedTime = "%02d:%02d".format(minutes, seconds)
                        Track(trackDto.trackName, trackDto.artistName, formattedTime)
                    }
                } else {
                    android.util.Log.e("TracksRepository", "Unexpected response type: ${response.javaClass}")
                    emptyList()
                }
            }
            else -> {
                android.util.Log.e("TracksRepository", "API returned error code: ${response.resultCode}, message: ${response.errorMessage}")
                return emptyList()
            }
        }
    }
}