package com.example.new_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.new_project.screens.FavoritesScreen
import com.example.new_project.screens.MainScreen
import com.example.new_project.screens.SongsScreen
import com.example.new_project.screens.TrackDetailsScreen
import com.example.new_project.ui.screens.NewPlaylistScreen
import com.example.new_project.ui.screens.PlaylistsScreen
import com.example.new_project.viewmodel.PlaylistsViewModel

@Composable
fun PlaylistHost(navController: NavHostController) {
    // Создаём ViewModel один раз для всей навигации
    val playlistsViewModel: PlaylistsViewModel = viewModel()

    fun openMain() {
        navController.navigate(PlaylistScreen.MAIN.name)
    }

    fun openSongs() {
        navController.navigate(PlaylistScreen.SONGS.name)
    }

    fun openPlaylists() {
        navController.navigate(PlaylistScreen.PLAYLISTS.name)
    }

    fun openFavorites() {
        navController.navigate(PlaylistScreen.FAVORITES.name)
    }

    fun openNewPlaylist() {
        navController.navigate(PlaylistScreen.NEW_PLAYLIST.name)
    }

    fun openTrackDetails() {
        navController.navigate(PlaylistScreen.TRACK_DETAILS.name)
    }

    fun goBack() {
        navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = PlaylistScreen.MAIN.name
    ) {
        composable(PlaylistScreen.MAIN.name) {
            MainScreen(
                onOpenSongs = { openSongs() },
                onOpenPlaylists = { openPlaylists() },
                onOpenFavorites = { openFavorites() }
            )
        }

        composable(PlaylistScreen.SONGS.name) {
            SongsScreen(
                onBack = { goBack() },
                onOpenTrackDetails = { openTrackDetails() }
            )
        }

        composable(PlaylistScreen.PLAYLISTS.name) {
            PlaylistsScreen(
                onBack = { goBack() },
                onOpenNewPlaylist = { openNewPlaylist() },
                viewModel = playlistsViewModel
            )
        }


        composable(PlaylistScreen.FAVORITES.name) {
            FavoritesScreen(
                onBack = { goBack() }
            )
        }

        composable(PlaylistScreen.NEW_PLAYLIST.name) {
            NewPlaylistScreen(
                onBack = { goBack() },
                viewModel = playlistsViewModel
            )
        }

        composable(PlaylistScreen.TRACK_DETAILS.name) {
            TrackDetailsScreen(
                onBack = { goBack() }
            )
        }
    }
}
