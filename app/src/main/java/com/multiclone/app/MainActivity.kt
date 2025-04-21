package com.multiclone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.multiclone.app.navigation.AppNavigation
import com.multiclone.app.ui.theme.MultiCloneAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize app components
        initializeComponents()
        
        setContent {
            MultiCloneAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    
    private fun initializeComponents() {
        lifecycleScope.launch {
            // Any initialization needed for app components
            // For example, checking permissions, initializing services, etc.
        }
    }
    
    // Handle back press using the new Android 14 back press API
    @androidx.annotation.OptIn(androidx.activity.BackEventCompat::class)
    override fun onBackPressed() {
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            // Let the system handle it for Android 14+
            super.onBackPressed()
        } else {
            // For pre-Android 14 devices
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}