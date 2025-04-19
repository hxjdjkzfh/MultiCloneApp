package com.multiclone.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.multiclone.app.ui.screens.about.AboutScreen
import com.multiclone.app.ui.screens.appselection.AppSelectionScreen
import com.multiclone.app.ui.screens.cloneconfig.CloneConfigScreen
import com.multiclone.app.ui.screens.home.HomeScreen
import com.multiclone.app.ui.screens.settings.SettingsScreen

/**
 * Navigation destinations for the app
 */
sealed class Screen(val route: String) {
    // Main screens
    object Home : Screen("home")
    object AppSelection : Screen("app_selection")
    object Settings : Screen("settings")
    object About : Screen("about")
    
    // Screens with parameters
    object CloneConfig : Screen("clone_config/{packageName}") {
        fun createRoute(packageName: String): String {
            return "clone_config/$packageName"
        }
    }
    
    object CloneDetail : Screen("clone_detail/{cloneId}") {
        fun createRoute(cloneId: String): String {
            return "clone_detail/$cloneId"
        }
    }
    
    object CloneSettings : Screen("clone_settings/{cloneId}") {
        fun createRoute(cloneId: String): String {
            return "clone_settings/$cloneId"
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
        // Home screen - displays list of cloned apps
        composable(Screen.Home.route) {
            HomeScreen(
                onAddCloneClick = {
                    navController.navigate(Screen.AppSelection.route)
                },
                onCloneLaunch = { /* Handled in ViewModel */ },
                onCloneInfo = { cloneId ->
                    navController.navigate(Screen.CloneDetail.createRoute(cloneId))
                },
                onCloneSettings = { cloneId ->
                    navController.navigate(Screen.CloneSettings.createRoute(cloneId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                }
            )
        }
        
        // App selection screen - displays list of installed apps to clone
        composable(Screen.AppSelection.route) {
            AppSelectionScreen(
                onAppSelected = { packageName ->
                    navController.navigate(Screen.CloneConfig.createRoute(packageName))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // Clone configuration screen - allows customization of clone
        composable(
            route = Screen.CloneConfig.route,
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            
            CloneConfigScreen(
                packageName = packageName,
                onCloneCreated = {
                    // Navigate back to home after clone is created
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // Settings screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // About screen
        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        // Clone detail screen
        composable(
            route = Screen.CloneDetail.route,
            arguments = listOf(
                navArgument("cloneId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cloneId = backStackEntry.arguments?.getString("cloneId") ?: ""
            
            // TODO: Implement clone detail screen
            
            // For now, just navigate back when loaded
            navController.navigateUp()
        }
        
        // Clone settings screen
        composable(
            route = Screen.CloneSettings.route,
            arguments = listOf(
                navArgument("cloneId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cloneId = backStackEntry.arguments?.getString("cloneId") ?: ""
            
            // TODO: Implement clone settings screen
            
            // For now, just navigate back when loaded
            navController.navigateUp()
        }
    }
}