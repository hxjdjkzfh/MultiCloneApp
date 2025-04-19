package com.multiclone.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.multiclone.app.ui.screens.about.AboutScreen
import com.multiclone.app.ui.screens.appselection.AppSelectionScreen
import com.multiclone.app.ui.screens.home.HomeScreen
import com.multiclone.app.ui.screens.settings.SettingsScreen
import timber.log.Timber

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AppSelection : Screen("app_selection")
    object CloneConfiguration : Screen("clone_config/{packageName}")
    object CloneDetails : Screen("clone_details/{cloneId}")
    object Settings : Screen("settings")
    object About : Screen("about")
    
    // Helper functions to create routes with parameters
    companion object {
        fun createCloneConfigRoute(packageName: String): String {
            return "clone_config/$packageName"
        }
        
        fun createCloneDetailsRoute(cloneId: String): String {
            return "clone_details/$cloneId"
        }
    }
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                onAddCloneClick = {
                    Timber.d("Navigating to app selection screen")
                    navController.navigate(Screen.AppSelection.route)
                },
                onCloneLaunch = { cloneId ->
                    Timber.d("Launching clone: $cloneId")
                    // No navigation needed - the clone will be launched by the view model
                },
                onCloneInfo = { cloneId ->
                    navController.navigate(Screen.createCloneDetailsRoute(cloneId))
                },
                onCloneSettings = { cloneId ->
                    navController.navigate(Screen.createCloneDetailsRoute(cloneId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                }
            )
        }
        
        // App selection screen
        composable(Screen.AppSelection.route) {
            AppSelectionScreen(
                onAppSelected = { packageName ->
                    Timber.d("Selected app: $packageName")
                    navController.navigate(Screen.createCloneConfigRoute(packageName))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Clone configuration screen
        composable(
            route = Screen.CloneConfiguration.route,
            arguments = listOf(
                navArgument("packageName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            
            Timber.d("Configuring clone for package: $packageName")
            
            // TODO: Implement CloneConfigurationScreen
            
            // Temporarily, we'll just go back to home
            navController.popBackStack(Screen.Home.route, false)
        }
        
        // Clone details screen
        composable(
            route = Screen.CloneDetails.route,
            arguments = listOf(
                navArgument("cloneId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val cloneId = backStackEntry.arguments?.getString("cloneId") ?: ""
            
            Timber.d("Viewing clone details: $cloneId")
            
            // TODO: Implement CloneDetailsScreen
            
            // Temporarily, we'll just go back
            navController.popBackStack()
        }
        
        // Settings screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // About screen
        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}