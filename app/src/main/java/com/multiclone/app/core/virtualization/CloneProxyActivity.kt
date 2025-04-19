package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multiclone.app.ui.theme.MultiCloneAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity that acts as a proxy to launch cloned apps
 * 
 * This activity receives a clone ID and package name, initializes the virtual environment,
 * and launches the cloned app in that environment
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show loading screen while setting up the cloned app
        setContent {
            MultiCloneAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        // Get clone ID and package name from intent
        val cloneId = intent.getStringExtra("clone_id")
        val packageName = intent.getStringExtra("package_name")
        
        if (cloneId != null && packageName != null) {
            // Launch the cloned app
            launchClonedApp(cloneId, packageName)
        } else {
            // Invalid parameters, finish activity
            finish()
        }
    }
    
    /**
     * Launch the cloned app in the virtual environment
     */
    private fun launchClonedApp(cloneId: String, packageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Initialize the virtual environment for this clone
                val environment = virtualAppEngine.initializeCloneEnvironment(cloneId, packageName)
                
                // Get the launch intent for the original app
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    // Modify the intent to run in our virtual environment
                    environment.prepareIntent(launchIntent)
                    
                    // Launch the app
                    startActivity(launchIntent)
                }
                
                // Close this proxy activity
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        }
    }
    
    companion object {
        /**
         * Helper method to launch a cloned app
         */
        fun launchClone(context: Context, packageName: String, cloneId: String) {
            val intent = Intent(context, CloneProxyActivity::class.java).apply {
                putExtra("clone_id", cloneId)
                putExtra("package_name", packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}