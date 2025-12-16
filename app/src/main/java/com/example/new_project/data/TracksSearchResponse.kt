package com.example.new_project.data

import com.example.new_project.domain.BaseResponse

class TracksSearchResponse(
    val results: List<TrackDto>
) : BaseResponse()
