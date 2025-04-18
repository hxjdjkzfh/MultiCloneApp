package com.multiclone.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multiclone.app.domain.virtualization.CloneEnvironment
import com.multiclone.app.ui.theme.MultiCloneTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Proxy activity that prepares and launches cloned applications in their isolated environments.
 * This activity acts as a bridge between the normal app environment and the virtualized environment.
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    companion object {
        private const val TAG = "CloneProxyActivity"
        
        // Extra keys for intent communication
        const val EXTRA_TARGET_PACKAGE = "target_package"
        const val EXTRA_TARGET_COMPONENT = "target_component"
        const val EXTRA_ENVIRONMENT_ID = "environment_id"
        const val EXTRA_CLONE_ID = "clone_id"
        const val EXTRA_CUSTOM_LABEL = "custom_label"
    }
    
    @Inject
    lateinit var cloneEnvironment: CloneEnvironment
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Extract parameters from intent
        val targetPackage = intent.getStringExtra(EXTRA_TARGET_PACKAGE) ?: run {
            Log.e(TAG, "No target package specified")
            finish()
            return
        }
        
        val environmentId = intent.getStringExtra(EXTRA_ENVIRONMENT_ID) ?: run {
            Log.e(TAG, "No environment ID specified")
            finish()
            return
        }
        
        val cloneId = intent.getStringExtra(EXTRA_CLONE_ID) ?: run {
            Log.e(TAG, "No clone ID specified")
            finish()
            return
        }
        
        // Show loading UI while preparing the environment
        setContent {
            MultiCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            
            // Launch the target app after preparing the environment
            LaunchedEffect(key1 = targetPackage) {
                // Slight delay to show loading UI
                delay(300)
                
                // Prepare and launch the cloned app
                prepareAndLaunchApp(targetPackage, environmentId, cloneId)
            }
        }
    }
    
    /**
     * Prepare the environment and launch the target application
     */
    private suspend fun prepareAndLaunchApp(
        packageName: String,
        environmentId: String,
        cloneId: String
    ) {
        try {
            Log.d(TAG, "Preparing environment $environmentId for $packageName")
            
            // Prepare the isolated environment
            cloneEnvironment.prepareEnvironment(environmentId)
            
            // Get the target component from intent, or find launch component
            val targetComponent = intent.getStringExtra(EXTRA_TARGET_COMPONENT) ?: run {
                // Try to find the main activity of the target app
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                launchIntent?.component?.className ?: run {
                    Log.e(TAG, "Unable to find main activity for $packageName")
                    finish()
                    return
                }
            }
            
            // Create an intent to launch the target component with virtualization
            val virtualIntent = Intent().apply {
                component = ComponentName(packageName, targetComponent)
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                
                // Pass environment information for potential interception by the virtualization layer
                putExtra("multiclone_environment_id", environmentId)
                putExtra("multiclone_clone_id", cloneId)
                
                // If we're launching from a custom shortcut with a unique URI, preserve it
                intent.data?.let { data = it }
                
                // Copy any other extras from the original intent
                intent.extras?.let { originalExtras ->
                    for (key in originalExtras.keySet()) {
                        if (!key.startsWith("multiclone_") && key != EXTRA_TARGET_PACKAGE && 
                            key != EXTRA_TARGET_COMPONENT && key != EXTRA_ENVIRONMENT_ID && 
                            key != EXTRA_CLONE_ID && key != EXTRA_CUSTOM_LABEL) {
                            
                            val value = originalExtras.get(key)
                            putExtra(key, value.toString())
                        }
                    }
                }
            }
            
            // Launch the virtualized app
            startActivity(virtualIntent)
            
            // Close this proxy activity
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error launching app", e)
            
            // Show error and close
            // In a production app, you'd want to show a nice error UI here
            finish()
        }
    }
    
    /**
     * Create a shortcut intent for this proxy activity
     */
    companion object {
        /**
         * Creates an intent suitable for a launcher shortcut
         */
        fun createShortcutIntent(
            context: Context,
            packageName: String,
            environmentId: String,
            cloneId: String,
            customLabel: String
        ): Intent {
            return Intent(context, CloneProxyActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                
                putExtra(EXTRA_TARGET_PACKAGE, packageName)
                putExtra(EXTRA_ENVIRONMENT_ID, environmentId)
                putExtra(EXTRA_CLONE_ID, cloneId)
                putExtra(EXTRA_CUSTOM_LABEL, customLabel)
                
                // Set a unique URI to ensure multiple shortcuts can exist
                data = Uri.parse("multiclone://$cloneId/$packageName")
            }
        }
    }
}