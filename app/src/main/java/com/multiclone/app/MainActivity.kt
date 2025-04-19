package com.multiclone.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.ui.navigation.AppNavHost
import com.multiclone.app.ui.theme.MultiCloneTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Main entry point for the MultiClone app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // Service connection
    private var cloneManagerService: CloneManagerService? = null
    private var bound = false
    
    // Service connection object
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as CloneManagerService.LocalBinder
            cloneManagerService = binder.getService()
            bound = true
            Timber.d("Service connected")
        }
        
        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
            cloneManagerService = null
            Timber.d("Service disconnected")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity - onCreate")
        
        // Start and bind to the CloneManagerService
        startAndBindService()
        
        // Handle intent if opened from a shortcut or notification
        handleIntent(intent)
        
        // Set up the UI
        setContent {
            MultiCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        // Bind to the service if not already bound
        if (!bound) {
            bindService()
        }
    }
    
    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    /**
     * Start and bind to the CloneManagerService
     */
    private fun startAndBindService() {
        // Start the service
        val intent = Intent(this, CloneManagerService::class.java)
        startService(intent)
        
        // Bind to the service
        bindService()
    }
    
    /**
     * Bind to the CloneManagerService
     */
    private fun bindService() {
        val intent = Intent(this, CloneManagerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
    
    /**
     * Handle intent when opening the app from a shortcut or notification
     */
    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            "com.multiclone.app.action.CREATE_CLONE" -> {
                // Get the package name
                val packageName = intent.getStringExtra("package_name")
                if (packageName != null) {
                    // Navigate to app selection screen with pre-selected package
                    lifecycleScope.launch {
                        // Implementation would navigate to the app selection screen
                        Timber.d("Navigating to create clone for package: $packageName")
                    }
                }
            }
            "com.multiclone.app.action.SHOW_ERROR" -> {
                // Get the error message
                val errorMessage = intent.getStringExtra("error_message")
                if (errorMessage != null) {
                    // Show error message
                    Timber.e("Error: $errorMessage")
                    // Implementation would show an error dialog or snackbar
                }
            }
        }
    }
}