package com.multiclone.app.navigation

import androidx.navigation.NavController

/**
 * Helper class to handle navigation actions throughout the app
 */
class AppNavigationActions(private val navController: NavController) {

    /**
     * Navigate to Home screen
     */
    fun navigateToHome() {
        navController.navigate(AppRoute.Home.route) {
            // Pop up to the start destination to avoid building up a large stack
            popUpTo(AppRoute.Home.route) {
                inclusive = true
            }
        }
    }

    /**
     * Navigate to App Selection screen
     */
    fun navigateToAppSelection() {
        navController.navigate(AppRoute.AppSelection.route)
    }

    /**
     * Navigate to Clone Setup screen
     * @param packageName The package name of the app to clone
     */
    fun navigateToCloneSetup(packageName: String) {
        val route = AppRoute.CloneSetup.route.replace("{packageName}", packageName)
        navController.navigate(route)
    }

    /**
     * Navigate to Settings screen
     */
    fun navigateToSettings() {
        navController.navigate(AppRoute.Settings.route)
    }

    /**
     * Navigate to About screen
     */
    fun navigateToAbout() {
        navController.navigate(AppRoute.About.route)
    }
}