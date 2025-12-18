package com.example.new_project.domain

data class Track(
    // Уникальный идентификатор (ОБЯЗАТЕЛЬНО!)
    val id: String,

    // Основные поля
    val trackName: String,
    val artistName: String,
    val trackTime: String,

    // Опциональные поля
    val albumName: String? = null,
    val artworkUrl: String? = null,
    val isFavorite: Boolean = false
)