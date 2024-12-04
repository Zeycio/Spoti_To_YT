package com.zeycio


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zeycio.presentation.screen.home.HomeScreen
import com.zeycio.presentation.screen.transfer.TransferScreen


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transfer : Screen("transfer/{playlistId}") {
        fun createRoute(playlistId: String) = "transfer/$playlistId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),activity: MainActivity
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                activity =activity,
                onPlaylistSelected = { playlist ->
                    navController.navigate(Screen.Transfer.createRoute(playlist.id))
                }
            )
        }

        composable(
            route = Screen.Transfer.route
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")
            requireNotNull(playlistId) { "playlistId parameter wasn't found. Please make sure it's passed in the navigation call." }

            TransferScreen(
                playlistId = playlistId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}