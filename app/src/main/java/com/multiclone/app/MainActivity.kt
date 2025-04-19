package com.multiclone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.ui.screens.home.HomeScreen
import com.multiclone.app.ui.theme.MultiCloneTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Main activity for the MultiClone app.
 * Serves as the entry point and hosts the Compose-based UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("MainActivity created")
        
        // Load clones when the app starts
        loadClones()
        
        setContent {
            MultiCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation would be implemented here in a real app
                    // For now, we'll just show the home screen
                    HomeScreen(
                        onNavigateToAppSelection = { /* Navigate to app selection */ },
                        onNavigateToSettings = { /* Navigate to settings */ },
                        onNavigateToAbout = { /* Navigate to about */ },
                        onNavigateToEditClone = { /* Navigate to edit clone */ }
                    )
                }
            }
        }
    }
    
    /**
     * Loads clones from the repository
     */
    private fun loadClones() {
        Timber.d("Loading clones from repository")
        // Launch a coroutine to load clones
        // This would be done by the view model in a real app
    }
}