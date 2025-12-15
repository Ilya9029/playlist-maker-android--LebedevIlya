package com.example.new_project.domain

import com.example.new_project.data.BaseResponse

interface NetworkClient {
    fun doRequest(dto: Any): BaseResponse
}
