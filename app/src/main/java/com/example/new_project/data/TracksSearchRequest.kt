package com.example.new_project.data

data class TracksSearchRequest(
    val expression: String,
    val limit: Int = 20,
    val entity: String = "song"
)