package com.multiclone.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.multiclone.app.ui.screens.about.AboutScreen
import com.multiclone.app.ui.screens.appselection.AppSelectionScreen
import com.multiclone.app.ui.screens.clonesetup.CloneSetupScreen
import com.multiclone.app.ui.screens.home.HomeScreen
import com.multiclone.app.ui.screens.settings.SettingsScreen

/**
 * Main navigation graph for the app
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Home screen
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(
                onNavigateToAppSelection = {
                    navController.navigate(AppDestinations.APP_SELECTION_ROUTE)
                },
                onNavigateToSettings = {
                    navController.navigate(AppDestinations.SETTINGS_ROUTE)
                },
                onNavigateToAbout = {
                    navController.navigate(AppDestinations.ABOUT_ROUTE)
                },
                onNavigateToEditClone = { cloneId ->
                    navController.navigate("${AppDestinations.EDIT_CLONE_ROUTE}/$cloneId")
                }
            )
        }
        
        // App selection screen
        composable(AppDestinations.APP_SELECTION_ROUTE) {
            AppSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCloneSetup = { packageName ->
                    navController.navigate("${AppDestinations.CLONE_SETUP_ROUTE}/$packageName")
                }
            )
        }
        
        // Clone setup screen
        composable(
            route = "${AppDestinations.CLONE_SETUP_ROUTE}/{packageName}",
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            
            CloneSetupScreen(
                packageName = packageName,
                onNavigateBack = { navController.popBackStack() },
                onCloneCreated = { _ ->
                    // Pop back to home screen
                    navController.popBackStack(AppDestinations.HOME_ROUTE, false)
                }
            )
        }
        
        // Settings screen
        composable(AppDestinations.SETTINGS_ROUTE) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = {
                    navController.navigate(AppDestinations.ABOUT_ROUTE)
                }
            )
        }
        
        // About screen
        composable(AppDestinations.ABOUT_ROUTE) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Edit clone screen (currently reuses the same screen as clone setup)
        composable(
            route = "${AppDestinations.EDIT_CLONE_ROUTE}/{cloneId}",
            arguments = listOf(
                navArgument("cloneId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cloneId = backStackEntry.arguments?.getString("cloneId") ?: ""
            
            // This will be implemented in the future
            // For now, just go back
            navController.popBackStack()
        }
    }
}