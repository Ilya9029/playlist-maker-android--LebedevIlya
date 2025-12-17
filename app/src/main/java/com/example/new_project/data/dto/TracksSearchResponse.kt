package com.example.new_project.data.dto

import com.example.new_project.domain.BaseResponse

/**
 * Модель ответа от iTunes Search API.
 * Содержит список треков в поле "results".
 */
data class TracksSearchResponse(
    val results: List<TrackDto> = emptyList()
) : BaseResponse()