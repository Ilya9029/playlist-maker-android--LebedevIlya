package com.example.new_project.ui.search

import com.example.new_project.data.Track

sealed class SearchState {
    object Initial : SearchState()          // экран только открылся
    object Searching : SearchState()        // идёт поиск
    data class Success(                     // успех, есть список треков
        val list: List<Track>
    ) : SearchState()
    data class Fail(                        // ошибка
        val error: String
    ) : SearchState()
}
