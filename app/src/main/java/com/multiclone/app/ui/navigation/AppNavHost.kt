package com.multiclone.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.multiclone.app.ui.screens.home.HomeScreen
import com.multiclone.app.ui.screens.appselection.AppSelectionScreen
import com.multiclone.app.ui.screens.cloneconfig.CloneConfigScreen
import com.multiclone.app.ui.screens.settings.SettingsScreen
import com.multiclone.app.ui.screens.about.AboutScreen

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppScreens.Home.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        // Home screen - displays list of cloned apps
        composable(AppScreens.Home.route) {
            HomeScreen(
                onNavigateToAppSelection = {
                    navController.navigate(AppScreens.AppSelection.route)
                },
                onNavigateToCloneConfig = { cloneId ->
                    navController.navigate("${AppScreens.CloneConfig.route}/$cloneId")
                },
                onNavigateToSettings = {
                    navController.navigate(AppScreens.Settings.route)
                },
                onNavigateToAbout = {
                    navController.navigate(AppScreens.About.route)
                }
            )
        }
        
        // App selection screen - select an app to clone
        composable(AppScreens.AppSelection.route) {
            AppSelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAppSelected = { packageName ->
                    // Navigate to clone configuration with the selected package name
                    navController.navigate("${AppScreens.CloneConfig.route}?packageName=$packageName")
                }
            )
        }
        
        // Clone configuration screen - configure a new clone or edit existing
        composable(
            route = "${AppScreens.CloneConfig.route}?packageName={packageName}&cloneId={cloneId}",
            arguments = listOf(
                navArgument("packageName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("cloneId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName")
            val cloneId = backStackEntry.arguments?.getString("cloneId")
            
            CloneConfigScreen(
                packageName = packageName,
                cloneId = cloneId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCloneCreated = {
                    // Navigate back to home after creating/updating the clone
                    navController.navigate(AppScreens.Home.route) {
                        // Clear the back stack up to the home screen
                        popUpTo(AppScreens.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        // Settings screen
        composable(AppScreens.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // About screen
        composable(AppScreens.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}