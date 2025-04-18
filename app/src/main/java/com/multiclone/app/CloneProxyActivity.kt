package com.multiclone.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.CloneEnvironment
import com.multiclone.app.domain.virtualization.CloneManagerService
import com.multiclone.app.ui.theme.MultiCloneAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Proxy activity that launches cloned applications
 * This activity is responsible for setting up and redirecting to the cloned app
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {

    companion object {
        private const val TAG = "CloneProxyActivity"
        
        // Intent extras
        const val EXTRA_CLONE_ID = "clone_id"
        const val EXTRA_ENVIRONMENT_ID = "environment_id"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_ORIGINAL_INTENT = "original_intent"
        
        // Intent action for launching clones
        const val ACTION_LAUNCH_CLONE = "com.multiclone.app.LAUNCH_CLONE"
        
        /**
         * Creates an Intent to launch a cloned app
         *
         * @param context The context
         * @param cloneId The ID of the clone to launch
         * @param packageName The package name of the app
         * @return The intent to launch the cloned app
         */
        fun createLaunchIntent(
            context: Context,
            cloneId: String,
            packageName: String
        ): Intent {
            return Intent(ACTION_LAUNCH_CLONE).apply {
                setPackage(context.packageName)
                putExtra(EXTRA_CLONE_ID, cloneId)
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    @Inject
    lateinit var cloneRepository: CloneRepository

    @Inject
    lateinit var cloneEnvironment: CloneEnvironment
    
    private var isLaunching by mutableStateOf(true)
    private var launchError by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "CloneProxyActivity created")
        
        // Set the theme for this loading activity
        setContent {
            MultiCloneAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        // Process intent
        processIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
        Log.d(TAG, "CloneProxyActivity received new intent")
        
        if (intent != null) {
            processIntent(intent)
        }
    }
    
    /**
     * Process the launch intent
     */
    private fun processIntent(intent: Intent) {
        val action = intent.action
        
        if (action == ACTION_LAUNCH_CLONE) {
            // Start the CloneManagerService if not already running
            Intent(this, CloneManagerService::class.java).also { serviceIntent ->
                startForegroundService(serviceIntent)
            }
            
            // Extract intent extras
            val cloneId = intent.getStringExtra(EXTRA_CLONE_ID)
            val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
            
            if (cloneId == null || packageName == null) {
                Log.e(TAG, "Missing required extras: cloneId=$cloneId, packageName=$packageName")
                launchError = "Missing required information to launch the app"
                finishAfterTransition()
                return
            }
            
            // Launch in the background
            lifecycleScope.launch {
                launchClonedApp(cloneId, packageName)
            }
        } else {
            Log.e(TAG, "Unknown action: $action")
            launchError = "Invalid action"
            finishAfterTransition()
        }
    }
    
    /**
     * Launch the cloned app
     */
    private suspend fun launchClonedApp(cloneId: String, packageName: String) {
        try {
            Log.d(TAG, "Launching cloned app: packageName=$packageName, cloneId=$cloneId")
            
            // Get the environment ID for this clone
            val environmentId = cloneEnvironment.getEnvironmentIdForClone(cloneId)
            if (environmentId == null) {
                Log.e(TAG, "No environment found for clone: $cloneId")
                launchError = "No environment found for this clone"
                finishAfterTransition()
                return
            }
            
            // Get the launch intent for the original app
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent == null) {
                Log.e(TAG, "No launch intent found for package: $packageName")
                launchError = "No launch intent found for this app"
                finishAfterTransition()
                return
            }
            
            // Clone this intent and modify it to launch in our virtual environment
            val virtualIntent = Intent(launchIntent)
            
            // Prepare virtual environment flags
            virtualIntent.putExtra("multiclone_clone_id", cloneId)
            virtualIntent.putExtra("multiclone_environment_id", environmentId)
            virtualIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            // In a real implementation, we would do much more complex proxying and sandbox setup here
            // For this demo, we'll just log that we would launch the app in a sandbox
            Log.i(TAG, "Would launch app $packageName in sandbox environment $environmentId for clone $cloneId")
            
            // Update the lastUsedTime for this clone
            cloneRepository.updateCloneLastUsed(cloneId, System.currentTimeMillis())
            
            // For demo purposes, we'll simulate a successful launch by briefly showing the loading screen
            delay(1000)
            
            // Finish this activity
            isLaunching = false
            finishAfterTransition()
            
            // For demo purposes, start the MainActivity
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(mainIntent)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error launching cloned app", e)
            launchError = "Error launching app: ${e.message}"
            finishAfterTransition()
        }
    }
}