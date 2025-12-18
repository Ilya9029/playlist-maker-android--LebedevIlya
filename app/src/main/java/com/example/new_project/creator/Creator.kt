package com.example.new_project.creator

import com.example.new_project.data.RetrofitNetworkClient
import com.example.new_project.data.network.api.ITunesApiService
import com.example.new_project.data.repository.PlaylistsRepository
import com.example.new_project.data.repository.PlaylistsRepositoryImpl
import com.example.new_project.data.repository.TracksRepository
import com.example.new_project.data.repository.TracksRepositoryImpl
import com.example.new_project.domain.NetworkClient
import com.example.new_project.ui.viewmodel.PlaylistViewModel
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private const val BASE_URL = "https://itunes.apple.com/"

    // 1. Создаем Retrofit экземпляр
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Создаем API сервис
    private val iTunesApiService: ITunesApiService by lazy {
        retrofit.create(ITunesApiService::class.java)
    }

    // 3. Создаем NetworkClient (✅ ИСПРАВЛЕНО: не nullable!)
    private val networkClient: NetworkClient by lazy {
        RetrofitNetworkClient(iTunesApiService)
    }

    fun getPlaylistsRepository(scope: CoroutineScope): PlaylistsRepository {
        return PlaylistsRepositoryImpl(scope)
    }

    fun getTracksRepository(scope: CoroutineScope): TracksRepository {
        return TracksRepositoryImpl(
            scope = scope,
            networkClient = networkClient  // ✅ Теперь точно не null
        )
    }

    fun createPlaylistViewModel(playlistId: Long): PlaylistViewModel {
        return PlaylistViewModel(playlistId = playlistId)
    }
}