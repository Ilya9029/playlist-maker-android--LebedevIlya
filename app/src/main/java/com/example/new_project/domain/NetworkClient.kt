package com.example.new_project.domain

interface NetworkClient {
    suspend fun doRequest(dto: Any): BaseResponse
}