package com.example.new_project.data.dto

import com.example.new_project.domain.BaseResponse
import com.google.gson.annotations.SerializedName

data class TracksSearchResponse(
    @SerializedName("resultCount")
    val resultCount: Int = 0,

    @SerializedName("results")
    val results: List<TrackDto> = emptyList()

) : BaseResponse(
    resultCode = 200,
    errorMessage = null
)