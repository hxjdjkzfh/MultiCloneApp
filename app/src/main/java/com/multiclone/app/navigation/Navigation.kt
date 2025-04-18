package com.multiclone.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.multiclone.app.ui.screens.AppSelectionScreen
import com.multiclone.app.ui.screens.CloneConfigScreen
import com.multiclone.app.ui.screens.ClonesListScreen
import com.multiclone.app.ui.screens.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AppSelection : Screen("app_selection")
    object CloneConfig : Screen("clone_config/{packageName}") {
        fun createRoute(packageName: String) = "clone_config/$packageName"
    }
    object ClonesList : Screen("clones_list")
}

@Composable
fun MultiCloneNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        
        composable(Screen.AppSelection.route) {
            AppSelectionScreen(navController)
        }
        
        composable(
            route = Screen.CloneConfig.route,
            arguments = listOf(
                navArgument("packageName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            CloneConfigScreen(navController, packageName)
        }
        
        composable(Screen.ClonesList.route) {
            ClonesListScreen(navController)
        }
    }
}
