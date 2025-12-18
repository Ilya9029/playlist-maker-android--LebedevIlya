package com.example.new_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.new_project.ui.screens.*
import com.example.new_project.ui.search.SearchViewModel
import com.example.new_project.ui.viewmodel.PlaylistViewModel
import com.example.new_project.ui.viewmodel.PlaylistsViewModel

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val playlistsViewModel: PlaylistsViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()

    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Main.route
    ) {
        composable(NavigationRoutes.Main.route) {
            MainScreen(
                onOpenSongs = { navigationActions.navigateToSongs() },
                onOpenPlaylists = { navigationActions.navigateToPlaylists() },
                onOpenFavorites = { navigationActions.navigateToFavorites() }
            )
        }

        composable(NavigationRoutes.Songs.route) {
            SongsScreen(
                onBack = { navigationActions.navigateBack() },
                onOpenTrackDetails = { trackId ->
                    navigationActions.navigateToTrackDetails(trackId)
                },
                viewModel = searchViewModel
            )
        }

        composable(NavigationRoutes.Playlists.route) {
            PlaylistsScreen(
                onBack = { navigationActions.navigateBack() },
                onOpenNewPlaylist = { navigationActions.navigateToNewPlaylist() },
                onOpenPlaylist = { playlistId ->
                    navigationActions.navigateToPlaylistDetails(playlistId)
                },
                viewModel = playlistsViewModel
            )
        }

        composable(NavigationRoutes.Favorites.route) {
            FavoritesScreen(
                onBack = { navigationActions.navigateBack() },
                onOpenTrackDetails = { trackId ->  // ← ДОБАВЛЕНО
                    navigationActions.navigateToTrackDetails(trackId)
                },
                viewModel = playlistsViewModel
            )
        }

        composable(NavigationRoutes.NewPlaylist.route) {
            NewPlaylistScreen(
                onBack = { navigationActions.navigateBack() },
                viewModel = playlistsViewModel
            )
        }

        composable(
            route = NavigationRoutes.TrackDetails.route,
            arguments = listOf(
                navArgument(NavigationRoutes.TRACK_ID_ARG) {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString(NavigationRoutes.TRACK_ID_ARG) ?: ""

            TrackDetailsScreen(
                trackId = trackId,
                onBack = { navigationActions.navigateBack() },
                searchViewModel = searchViewModel,
                playlistsViewModel = playlistsViewModel
            )
        }

        composable(
            route = NavigationRoutes.PlaylistDetails.route,
            arguments = listOf(
                navArgument(NavigationRoutes.PLAYLIST_ID_ARG) {
                    type = androidx.navigation.NavType.LongType
                }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong(NavigationRoutes.PLAYLIST_ID_ARG) ?: 0L

            // Создаем ViewModel с ID плейлиста
            val playlistViewModel = PlaylistViewModel(playlistId = playlistId)

            PlaylistScreen(
                playlistId = playlistId,
                viewModel = playlistViewModel,
                onBack = { navigationActions.navigateBack() },
                onOpenTrackDetails = { trackId ->  // ← ДОБАВЛЕНО
                    navigationActions.navigateToTrackDetails(trackId)
                }
            )
        }
    }
}