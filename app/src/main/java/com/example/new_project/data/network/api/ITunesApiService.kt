package com.example.new_project.data.network.api

import com.example.new_project.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Интерфейс для работы с ITunes Search API.
 *
 * Документация: https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/index.html
 */
interface ITunesApiService {

    /**
     * Поиск треков по заданному термину.
     *
     * @param term - поисковый запрос (например, "Queen")
     * @param entity - тип результата (по умолчанию "song")
     * @param limit - количество результатов (по умолчанию 20)
     * @return Объект [TracksSearchResponse], содержащий список треков
     */
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 20
    ): TracksSearchResponse
}