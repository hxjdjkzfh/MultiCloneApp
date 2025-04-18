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

/**
 * Main navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ClonesList : Screen("clones_list")
    object AppSelection : Screen("app_selection")
    object CloneConfig : Screen("clone_config/{packageName}") {
        fun createRoute(packageName: String) = "clone_config/$packageName"
    }
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToClonesList = {
                    navController.navigate(Screen.ClonesList.route)
                },
                onNavigateToAppSelection = {
                    navController.navigate(Screen.AppSelection.route)
                }
            )
        }
        
        composable(Screen.ClonesList.route) {
            ClonesListScreen(
                onNavigateUp = {
                    navController.navigateUp()
                },
                onNavigateToAppSelection = {
                    navController.navigate(Screen.AppSelection.route)
                }
            )
        }
        
        composable(Screen.AppSelection.route) {
            AppSelectionScreen(
                onNavigateUp = {
                    navController.navigateUp()
                },
                onAppSelected = { packageName ->
                    navController.navigate(
                        Screen.CloneConfig.createRoute(packageName)
                    )
                }
            )
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
            
            CloneConfigScreen(
                packageName = packageName,
                onNavigateUp = {
                    navController.navigateUp()
                },
                onCloneCreated = {
                    // Navigate back to home screen after clone creation
                    navController.navigate(Screen.Home.route) {
                        // Clear the back stack so the user can't navigate back to the config screen
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}