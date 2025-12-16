package com.example.new_project.data

import com.example.new_project.creator.Storage
import com.example.new_project.domain.BaseResponse
import com.example.new_project.domain.NetworkClient

class RetrofitNetworkClient(
    private val storage: Storage
) : NetworkClient {

    override fun doRequest(request: Any): BaseResponse {
        val searchList =
            storage.search((request as TracksSearchRequest).expression)
        return TracksSearchResponse(searchList).apply {
            resultCode = 200
        }
    }
}
