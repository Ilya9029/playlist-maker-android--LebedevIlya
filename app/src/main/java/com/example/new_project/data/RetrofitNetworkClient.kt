// data/RetrofitNetworkClient.kt

package com.example.new_project.data

import com.example.new_project.data.dto.TracksSearchResponse
import com.example.new_project.data.network.api.ITunesApiService
import com.example.new_project.domain.BaseResponse
import com.example.new_project.domain.NetworkClient

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): BaseResponse {
        return when (dto) {
            is TracksSearchRequest -> {
                try {
                    val response = iTunesApiService.searchTracks(term = dto.expression)
                    TracksSearchResponse(results = response.results).apply {
                        resultCode = 200
                    }
                } catch (e: Exception) {
                    TracksSearchResponse(results = emptyList()).apply {
                        resultCode = 500
                        errorMessage = e.message
                    }
                }
            }
            else -> {
                TracksSearchResponse(results = emptyList()).apply {
                    resultCode = 400
                    errorMessage = "Unknown request type"
                }
            }
        }
    }
}