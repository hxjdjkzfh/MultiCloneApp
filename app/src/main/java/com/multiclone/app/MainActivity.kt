package com.multiclone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.multiclone.app.core.virtualization.CloneProxyActivity
import com.multiclone.app.ui.screens.AppSelectionScreen
import com.multiclone.app.ui.screens.CloneConfigScreen
import com.multiclone.app.ui.screens.CloneDetailsScreen
import com.multiclone.app.ui.screens.HomeScreen
import com.multiclone.app.ui.theme.MultiCloneAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultiCloneAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MultiCloneApp()
                }
            }
        }
    }
}

/**
 * Main composable that sets up the app navigation
 */
@Composable
fun MultiCloneApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        // Home screen with list of clones
        composable(route = "home") {
            HomeScreen(
                onNavigateToAppSelection = {
                    navController.navigate("app_selection")
                },
                onNavigateToCloneDetails = { cloneId ->
                    navController.navigate("clone_details/$cloneId")
                }
            )
        }
        
        // App selection screen
        composable(route = "app_selection") {
            AppSelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAppSelected = { packageName ->
                    navController.navigate("clone_config/$packageName")
                }
            )
        }
        
        // Clone configuration screen
        composable(
            route = "clone_config/{packageName}",
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            CloneConfigScreen(
                packageName = packageName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCloneCreated = { cloneId ->
                    // Navigate back to home when clone is created
                    navController.popBackStack(route = "home", inclusive = false)
                }
            )
        }
        
        // Clone details screen
        composable(
            route = "clone_details/{cloneId}",
            arguments = listOf(
                navArgument("cloneId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cloneId = backStackEntry.arguments?.getString("cloneId") ?: ""
            CloneDetailsScreen(
                cloneId = cloneId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLaunchClone = { packageName, id ->
                    // Launch the cloned app using the proxy activity
                    CloneProxyActivity.launchClone(navController.context, packageName, id)
                },
                onDeleteClone = { _ ->
                    // Return to home after deleting
                    navController.popBackStack()
                }
            )
        }
    }
}