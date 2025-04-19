package com.multiclone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.multiclone.app.ui.screens.AppSelectionScreen
import com.multiclone.app.ui.screens.CloneConfigScreen
import com.multiclone.app.ui.screens.HomeScreen
import com.multiclone.app.ui.theme.MultiCloneAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MultiCloneAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "home") {
                        // Home screen with list of clones
                        composable("home") {
                            HomeScreen(
                                onAddCloneClick = { navController.navigate("app_selection") },
                                onCloneClick = { cloneId -> 
                                    navController.navigate("clone_details/$cloneId")
                                }
                            )
                        }
                        
                        // App selection screen
                        composable("app_selection") {
                            AppSelectionScreen(
                                onAppSelected = { packageName ->
                                    navController.navigate("clone_config/$packageName")
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        
                        // Clone configuration screen
                        composable("clone_config/{packageName}") { backStackEntry ->
                            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
                            CloneConfigScreen(
                                packageName = packageName,
                                onBackClick = { navController.popBackStack() },
                                onCloneCreated = { cloneId ->
                                    // Navigate back to home
                                    navController.popBackStack("home", false)
                                }
                            )
                        }
                        
                        // Clone details screen
                        composable("clone_details/{cloneId}") { backStackEntry ->
                            val cloneId = backStackEntry.arguments?.getString("cloneId") ?: ""
                            
                            // TODO: Replace with actual CloneDetailsScreen
                            HomeScreen(
                                onAddCloneClick = { navController.navigate("app_selection") },
                                onCloneClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}