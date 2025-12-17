package com.example.new_project.creator

import com.example.new_project.data.RetrofitNetworkClient
import com.example.new_project.data.TracksRepositoryImpl
import com.example.new_project.data.network.api.ITunesApiService
import com.example.new_project.domain.TracksRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val BASE_URL = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApiService = retrofit.create(ITunesApiService::class.java)

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(iTunesApiService)
        )
    }
}