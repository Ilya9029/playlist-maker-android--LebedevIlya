package com.example.new_project.ui.navigation

sealed class NavigationRoutes(val route: String) {
    object Main : NavigationRoutes("main")
    object Songs : NavigationRoutes("songs")
    object Playlists : NavigationRoutes("playlists")
    object Favorites : NavigationRoutes("favorites")
    object Settings : NavigationRoutes("settings")  // ✅ ДОБАВЛЕНО
    object NewPlaylist : NavigationRoutes("new_playlist")
    object TrackDetails : NavigationRoutes("track_details/{trackId}") {
        fun createRoute(trackId: String) = "track_details/$trackId"
    }
    object PlaylistDetails : NavigationRoutes("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }

    companion object {
        const val TRACK_ID_ARG = "trackId"
        const val PLAYLIST_ID_ARG = "playlistId"
    }
}