package com.multiclone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.multiclone.app.ui.theme.MultiCloneTheme
import com.multiclone.app.ui.screens.home.HomeScreen
import com.multiclone.app.ui.screens.appselection.AppSelectionScreen
import com.multiclone.app.ui.screens.settings.SettingsScreen
import com.multiclone.app.ui.screens.about.AboutScreen
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Main Activity for the MultiClone App.
 * Sets up navigation and theming.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity created")
        
        setContent {
            MultiCloneTheme {
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
 * Composable that builds the main navigation structure of the app
 */
@Composable
fun MultiCloneApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToAppSelection = { navController.navigate("appSelection") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAbout = { navController.navigate("about") }
            )
        }
        composable("appSelection") {
            AppSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onAppSelected = { appId -> 
                    // Navigate to clone config with app ID
                    navController.popBackStack()
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("about") {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}