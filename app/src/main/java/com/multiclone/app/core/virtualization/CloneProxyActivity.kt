package com.multiclone.app.core.virtualization

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.multiclone.app.domain.service.VirtualAppService
import com.multiclone.app.ui.theme.MultiCloneTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Proxy activity that launches the cloned app
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "CloneProxyActivity"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_VIRTUAL_ENV_ID = "extra_virtual_env_id"
    }
    
    @Inject
    lateinit var virtualAppService: VirtualAppService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        val virtualEnvId = intent.getStringExtra(EXTRA_VIRTUAL_ENV_ID)
        
        if (packageName == null || virtualEnvId == null) {
            Log.e(TAG, "Missing required extras: packageName=$packageName, virtualEnvId=$virtualEnvId")
            finish()
            return
        }
        
        setContent {
            MultiCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchingScreen(packageName, virtualEnvId) {
                        finish()
                    }
                }
            }
        }
    }
    
    /**
     * Launches the cloned app in a separate process
     */
    private fun launchApp(packageName: String, virtualEnvId: String) {
        try {
            // Get the launch intent for the app
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            
            if (launchIntent != null) {
                // Clone the intent and add our virtualization parameters
                val cloneIntent = Intent(launchIntent)
                cloneIntent.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("virtual_env_id", virtualEnvId)
                    // The activity would be started in the target app's process
                    // but with our virtualization layer injected
                }
                
                // Start the service that will handle the virtualization
                val serviceIntent = Intent(this, CloneManagerService::class.java).apply {
                    putExtra(CloneManagerService.EXTRA_PACKAGE_NAME, packageName)
                    putExtra(CloneManagerService.EXTRA_VIRTUAL_ENV_ID, virtualEnvId)
                    putExtra(CloneManagerService.EXTRA_ACTION, CloneManagerService.ACTION_PREPARE_LAUNCH)
                }
                startService(serviceIntent)
                
                // Start the actual app
                startActivity(cloneIntent)
                
                Log.d(TAG, "Launched cloned app: $packageName with environment: $virtualEnvId")
            } else {
                Log.e(TAG, "No launch intent found for package: $packageName")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app: $packageName", e)
        }
    }
    
    /**
     * Composable function showing loading screen while launching
     */
    @Composable
    private fun LaunchingScreen(
        packageName: String,
        virtualEnvId: String,
        onFinish: () -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            
            Text(
                text = "Launching cloned app...",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        
        LaunchedEffect(packageName, virtualEnvId) {
            // Brief delay to show loading UI
            delay(1000)
            
            // Launch the app
            launchApp(packageName, virtualEnvId)
            
            // Finish this activity
            delay(500)
            onFinish()
        }
    }
}