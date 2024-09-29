package com.example.videosapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.videosapp.presentation.screens.list.VideoListScreen
import com.example.videosapp.presentation.screens.player.PlayerScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()
    val playerScreenIndexParameter="index"
    NavHost(
        navController = navController,
        startDestination = Screens.LIST_SCREEN.route,
    ) {
        composable(Screens.LIST_SCREEN.route) { VideoListScreen(navController) }
        composable(
            route = "${Screens.PLAYER_SCREEN}/{$playerScreenIndexParameter}",
            arguments = listOf(navArgument(playerScreenIndexParameter) { type = NavType.LongType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getLong(playerScreenIndexParameter) ?: 0
            PlayerScreen(navController, videoId)
        }
    }
}