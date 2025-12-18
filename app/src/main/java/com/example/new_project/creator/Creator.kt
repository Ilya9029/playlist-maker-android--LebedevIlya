package com.example.new_project.creator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.new_project.data.RetrofitNetworkClient
import com.example.new_project.data.database.AppDatabase
import com.example.new_project.data.network.api.ITunesApiService
import com.example.new_project.data.preferences.SearchHistoryPreferences
import com.example.new_project.data.repository.PlaylistsRepository
import com.example.new_project.data.repository.PlaylistsRepositoryImpl
import com.example.new_project.data.repository.TracksRepository
import com.example.new_project.data.repository.TracksRepositoryImpl
import com.example.new_project.domain.NetworkClient
import com.example.new_project.ui.search.SearchViewModel
import com.example.new_project.ui.viewmodel.PlaylistViewModel
import com.example.new_project.ui.viewmodel.PlaylistsViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Расширение для DataStore (должно быть вне класса)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

object Creator {
    private const val BASE_URL = "https://itunes.apple.com/"

    // ========== RETROFIT (СЕТЬ) ==========

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

    // 3. Создаем NetworkClient
    private val networkClient: NetworkClient by lazy {
        RetrofitNetworkClient(iTunesApiService)
    }

    // ========== ROOM (БАЗА ДАННЫХ) ==========

    // 4. Создаем базу данных Room (требует Context!)
    private lateinit var appDatabase: AppDatabase

    // ========== DATASTORE (ИСТОРИЯ ПОИСКА) ==========

    // 5. Создаем DataStore для истории поиска
    private lateinit var searchHistoryPreferences: SearchHistoryPreferences

    /**
     * Инициализирует Creator с контекстом приложения.
     * Должен быть вызван в Application классе или в onCreate() MainActivity.
     */
    fun init(context: Context) {
        if (!::appDatabase.isInitialized) {
            appDatabase = AppDatabase.getInstance(context.applicationContext)
        }

        if (!::searchHistoryPreferences.isInitialized) {
            searchHistoryPreferences = SearchHistoryPreferences(context.dataStore)
        }
    }

    // ========== РЕПОЗИТОРИИ ==========

    fun getPlaylistsRepository(): PlaylistsRepository {
        checkDatabaseInitialized()
        return PlaylistsRepositoryImpl(
            playlistDao = appDatabase.playlistDao()
        )
    }

    fun getTracksRepository(): TracksRepository {
        checkDatabaseInitialized()
        return TracksRepositoryImpl(
            trackDao = appDatabase.trackDao(),
            playlistDao = appDatabase.playlistDao(),
            networkClient = networkClient
        )
    }

    // ========== PREFERENCES ==========

    fun getSearchHistoryPreferences(): SearchHistoryPreferences {
        if (!::searchHistoryPreferences.isInitialized) {
            throw IllegalStateException(
                "SearchHistoryPreferences не инициализирован. Вызовите Creator.init(context) перед использованием."
            )
        }
        return searchHistoryPreferences
    }

    // ========== VIEWMODEL ФАБРИКИ ==========

    fun createPlaylistsViewModel(): PlaylistsViewModel {
        return PlaylistsViewModel(
            playlistsRepository = getPlaylistsRepository(),
            tracksRepository = getTracksRepository()
        )
    }

    fun createSearchViewModel(): SearchViewModel {
        return SearchViewModel(
            repository = getTracksRepository(),
            searchHistoryPreferences = getSearchHistoryPreferences()
        )
    }

    fun createPlaylistViewModel(playlistId: Long): PlaylistViewModel {
        return PlaylistViewModel(
            playlistId = playlistId,
            playlistsRepository = getPlaylistsRepository()
        )
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private fun checkDatabaseInitialized() {
        if (!::appDatabase.isInitialized) {
            throw IllegalStateException(
                "AppDatabase не инициализирован. Вызовите Creator.init(context) перед использованием."
            )
        }
    }

    /**
     * Для тестирования: получает тестовую базу данных (in-memory).
     */
    fun getTestDatabase(context: Context): AppDatabase {
        return AppDatabase.getTestInstance(context)
    }
}