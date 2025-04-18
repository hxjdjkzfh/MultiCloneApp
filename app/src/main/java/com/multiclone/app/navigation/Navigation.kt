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
    object ClonesList : Screen("clones_list")
    object AppSelection : Screen("app_selection")
    object CloneConfig : Screen("clone_config/{packageName}") {
        fun createRoute(packageName: String): String = "clone_config/$packageName"
    }
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToClonesList = { navController.navigate(Screen.ClonesList.route) },
                onNavigateToAppSelection = { navController.navigate(Screen.AppSelection.route) }
            )
        }
        
        composable(Screen.ClonesList.route) {
            ClonesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAppSelection = { navController.navigate(Screen.AppSelection.route) }
            )
        }
        
        composable(Screen.AppSelection.route) {
            AppSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onAppSelected = { packageName ->
                    navController.navigate(Screen.CloneConfig.createRoute(packageName))
                }
            )
        }
        
        composable(
            route = Screen.CloneConfig.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            CloneConfigScreen(
                packageName = packageName,
                onNavigateBack = { navController.popBackStack() },
                onCloneCreated = { 
                    // Navigate to clones list after creating a clone
                    navController.popBackStack(Screen.ClonesList.route, inclusive = false)
                }
            )
        }
    }
}