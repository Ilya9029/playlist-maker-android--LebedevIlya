package com.example.new_project.ui.navigation

import androidx.navigation.NavHostController

class NavigationActions(private val navController: NavHostController) {
    fun navigateToMain() {
        navController.navigate(NavigationRoutes.Main.route)
    }

    fun navigateToSongs() {
        navController.navigate(NavigationRoutes.Songs.route)
    }

    fun navigateToPlaylists() {
        navController.navigate(NavigationRoutes.Playlists.route)
    }

    fun navigateToFavorites() {
        navController.navigate(NavigationRoutes.Favorites.route)
    }

    fun navigateToSettings() {  // ✅ ДОБАВЛЕНО
        navController.navigate(NavigationRoutes.Settings.route)
    }

    fun navigateToNewPlaylist() {
        navController.navigate(NavigationRoutes.NewPlaylist.route)
    }

    fun navigateToTrackDetails(trackId: String) {
        navController.navigate(NavigationRoutes.TrackDetails.createRoute(trackId))
    }

    fun navigateToPlaylistDetails(playlistId: Long) {
        navController.navigate(NavigationRoutes.PlaylistDetails.createRoute(playlistId))
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}