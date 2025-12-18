package com.example.new_project.domain

sealed class SearchState {
    object Initial : SearchState()
    object Searching : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()  // ← должно быть так
    data class Fail(val message: String) : SearchState()
}