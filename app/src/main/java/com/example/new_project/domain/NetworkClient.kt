package com.example.new_project.domain

interface NetworkClient {
    fun doRequest(dto: Any): BaseResponse
}
