package com.example.new_project.data

import com.example.new_project.domain.NetworkClient
import com.example.new_project.domain.TracksRepository

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        kotlinx.coroutines.delay(1000) // просто ждём секунду

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                val seconds = it.trackTimeMillis / 1000
                val minutes = seconds / 60
                val trackTime = "%02d".format(minutes) + ":" +
                        "%02d".format(seconds - minutes * 60)

                Track(it.trackName, it.artistName, trackTime)
            }
        } else {
            emptyList()
        }
    }
}
