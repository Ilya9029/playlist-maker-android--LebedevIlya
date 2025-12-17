package com.example.new_project.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("trackName")
    val trackName: String,

    @SerializedName("artistName")
    val artistName: String,

    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long,

    @SerializedName("artworkUrl100")
    val artworkUrl: String?,

    @SerializedName("previewUrl")
    val previewUrl: String?,

    @SerializedName("collectionName")
    val albumName: String?
)