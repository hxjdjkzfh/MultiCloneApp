package com.multiclone.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo

/**
 * Main home screen that handles navigation between different screens
 */
@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "clones") {
        // Clones list screen
        composable("clones") {
            ClonesListScreen(
                onNavigateToAppSelection = {
                    navController.navigate("app_selection")
                }
            )
        }
        
        // App selection screen
        composable("app_selection") {
            AppSelectionScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onAppSelected = { app ->
                    navController.navigate("clone_config/${app.packageName}")
                }
            )
        }
        
        // Clone configuration screen
        composable(
            route = "clone_config/{packageName}",
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            CloneConfigScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onCloneCreated = { clone ->
                    // Navigate back to clones list
                    navController.popBackStack(
                        route = "clones",
                        inclusive = false
                    )
                }
            )
        }
    }
}