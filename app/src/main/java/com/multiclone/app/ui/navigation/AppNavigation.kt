package com.multiclone.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.multiclone.app.ui.screens.appselection.AppSelectionScreen
import com.multiclone.app.ui.screens.clonesetup.CloneSetupScreen
import com.multiclone.app.ui.screens.home.HomeScreen

/**
 * Navigation routes for the application.
 */
object Routes {
    const val HOME = "home"
    const val APP_SELECTION = "app_selection"
    const val CLONE_SETUP = "clone_setup/{packageName}"
    
    // Helper function to create a CLONE_SETUP route with a specific package name
    fun cloneSetupRoute(packageName: String): String {
        return "clone_setup/$packageName"
    }
}

/**
 * Main navigation component for the app.
 * Sets up navigation routes between different screens.
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        // Home screen - main dashboard for managing cloned apps
        composable(Routes.HOME) {
            HomeScreen(
                onAddNewClone = {
                    navController.navigate(Routes.APP_SELECTION)
                },
                onCloneSelected = { /* TODO: Add clone management */ }
            )
        }
        
        // App selection screen - for choosing which app to clone
        composable(Routes.APP_SELECTION) {
            AppSelectionScreen(
                onAppSelected = { packageName ->
                    navController.navigate(Routes.cloneSetupRoute(packageName))
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        
        // Clone setup screen - for configuring clone settings
        composable(
            Routes.CLONE_SETUP,
            arguments = listOf(
                navArgument("packageName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            CloneSetupScreen(
                packageName = packageName,
                onBackPressed = {
                    navController.popBackStack()
                },
                onSetupComplete = {
                    // Navigate back to home screen after clone is created
                    navController.navigate(Routes.HOME) {
                        // Clear backstack up to home
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}