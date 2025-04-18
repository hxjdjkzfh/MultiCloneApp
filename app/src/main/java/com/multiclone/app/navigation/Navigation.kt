package com.multiclone.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.multiclone.app.ui.screens.AppSelectionScreen
import com.multiclone.app.ui.screens.CloneConfigScreen
import com.multiclone.app.ui.screens.ClonesListScreen
import com.multiclone.app.ui.screens.HomeScreen

/**
 * Sealed class containing all the routes for the app
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
 * App navigation setup using Jetpack Navigation Compose
 */
@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        // Home screen - Main entry point
        composable(route = Screen.Home.route) {
            HomeScreen(
                navigateToClonesList = {
                    navController.navigate(Screen.ClonesList.route)
                },
                navigateToCreateClone = {
                    navController.navigate(Screen.AppSelection.route)
                }
            )
        }
        
        // Clones list screen - Shows all cloned apps
        composable(route = Screen.ClonesList.route) {
            ClonesListScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                navigateToCreateClone = {
                    navController.navigate(Screen.AppSelection.route)
                }
            )
        }
        
        // App selection screen - Choose which app to clone
        composable(route = Screen.AppSelection.route) {
            AppSelectionScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onAppSelected = { packageName ->
                    navController.navigate(Screen.CloneConfig.createRoute(packageName))
                }
            )
        }
        
        // Clone configuration screen - Set up clone settings
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
                onBackPressed = {
                    navController.popBackStack()
                },
                onCloneCreated = {
                    // Navigate to clones list and clear the back stack
                    navController.navigate(Screen.ClonesList.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
    }
}