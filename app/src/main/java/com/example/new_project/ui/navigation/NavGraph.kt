package com.example.new_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.new_project.creator.Creator
import com.example.new_project.ui.screens.*
import com.example.new_project.ui.search.SearchViewModel
import com.example.new_project.ui.viewmodel.PlaylistViewModel
import com.example.new_project.ui.viewmodel.PlaylistsViewModel

@Composable
fun NavGraph(
    navController: NavHostController
) {
    // Инициализируем Creator с контекстом
    val context = LocalContext.current
    Creator.init(context)

    // Создаем ViewModel через Creator
    val playlistsViewModel: PlaylistsViewModel = viewModel(
        factory = PlaylistsViewModelFactory()
    )

    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory()
    )

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
                onOpenTrackDetails = { trackId ->
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

            // Создаем ViewModel через Creator
            val playlistViewModel = Creator.createPlaylistViewModel(playlistId)

            PlaylistScreen(
                playlistId = playlistId,
                viewModel = playlistViewModel,
                onBack = { navigationActions.navigateBack() },
                onOpenTrackDetails = { trackId ->
                    navigationActions.navigateToTrackDetails(trackId)
                }
            )
        }
    }
}

// Фабрики для ViewModel
class PlaylistsViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return Creator.createPlaylistsViewModel() as T
    }
}

class SearchViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return Creator.createSearchViewModel() as T
    }
}