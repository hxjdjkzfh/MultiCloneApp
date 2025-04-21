package com.multiclone.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.multiclone.app.ui.screens.home.HomeScreen
import com.multiclone.app.ui.screens.appselection.AppSelectionScreen
import com.multiclone.app.ui.screens.clonesetup.CloneSetupScreen
import com.multiclone.app.ui.screens.settings.SettingsScreen
import com.multiclone.app.ui.screens.about.AboutScreen

/**
 * Navigation routes for the app
 */
sealed class AppRoute(val route: String) {
    object Home : AppRoute("home")
    object AppSelection : AppRoute("app_selection")
    object CloneSetup : AppRoute("clone_setup/{packageName}")
    object Settings : AppRoute("settings")
    object About : AppRoute("about")
    
    // Helper function for constructing parameterized routes
    fun createRoute(vararg params: String): String {
        return buildString {
            append(route)
            params.forEach { param ->
                route.replace("{$param}", param)
            }
        }
    }
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Create navigation helper
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }
    
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route
    ) {
        // Home screen - displays the list of cloned apps
        composable(AppRoute.Home.route) {
            HomeScreen(
                onAppSelectionClick = { navigationActions.navigateToAppSelection() },
                onSettingsClick = { navigationActions.navigateToSettings() },
                onAboutClick = { navigationActions.navigateToAbout() },
                onCloneClick = { /* Handle clone launch */ }
            )
        }
        
        // App selection screen - displays list of installed apps
        composable(AppRoute.AppSelection.route) {
            AppSelectionScreen(
                onAppSelected = { packageName -> 
                    navigationActions.navigateToCloneSetup(packageName)
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Clone setup screen - configures a new clone
        composable(AppRoute.CloneSetup.route) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            CloneSetupScreen(
                packageName = packageName,
                onBackClick = { navController.popBackStack() },
                onCloneCreated = { 
                    navigationActions.navigateToHome()
                }
            )
        }
        
        // Settings screen
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // About screen
        composable(AppRoute.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}