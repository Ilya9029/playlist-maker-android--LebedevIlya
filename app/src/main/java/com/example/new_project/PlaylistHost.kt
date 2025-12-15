package com.example.new_project

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun PlaylistHost(navController: NavHostController) {

    fun openMain() {
        navController.navigate(PlaylistScreen.MAIN.name)
    }

    fun openSearch() {
        navController.navigate(PlaylistScreen.SEARCH.name)
    }

    fun openSettings() {
        navController.navigate(PlaylistScreen.SETTINGS.name)
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
                onOpenSearch = { openSearch() },
                onOpenSettings = { openSettings() }
            )
        }

        composable(PlaylistScreen.SEARCH.name) {
            SearchScreen(
                onBack = { goBack() }
            )
        }

        composable(PlaylistScreen.SETTINGS.name) {
            SettingsScreen(
                onBack = { goBack() }
            )
        }
    }
}
