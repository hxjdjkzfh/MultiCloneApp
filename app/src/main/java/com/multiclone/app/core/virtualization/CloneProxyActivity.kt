package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.ui.theme.MultiCloneAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

/**
 * Activity that serves as a proxy for launching cloned apps
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    
    @Inject
    lateinit var virtualizationService: VirtualizationService
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val cloneId = intent.getStringExtra(VirtualAppEngine.EXTRA_CLONE_ID)
        val packageName = intent.getStringExtra(VirtualAppEngine.EXTRA_PACKAGE_NAME)
        
        if (cloneId.isNullOrEmpty() || packageName.isNullOrEmpty()) {
            Timber.e("Missing clone ID or package name")
            finish()
            return
        }
        
        Timber.d("Launching cloned app: $cloneId, package: $packageName")
        
        setContent {
            MultiCloneAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchScreen(
                        cloneId = cloneId,
                        packageName = packageName,
                        onLaunchComplete = { success ->
                            if (!success) {
                                // If launch fails, finish the activity
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Update the running state when the activity is destroyed
        val cloneId = intent.getStringExtra(VirtualAppEngine.EXTRA_CLONE_ID)
        if (!cloneId.isNullOrEmpty()) {
            // Run in a new thread to avoid blocking the main thread
            Thread {
                try {
                    cloneRepository.updateCloneRunningState(cloneId, false)
                } catch (e: Exception) {
                    Timber.e(e, "Error updating running state on destroy")
                }
            }.start()
        }
    }
    
    /**
     * Screen shown while launching a cloned app
     */
    @Composable
    private fun LaunchScreen(
        cloneId: String,
        packageName: String,
        onLaunchComplete: (Boolean) -> Unit
    ) {
        var cloneInfo by remember { mutableStateOf<CloneInfo?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        
        // Load the clone info
        LaunchedEffect(cloneId) {
            try {
                cloneInfo = cloneRepository.getCloneById(cloneId)
                if (cloneInfo == null) {
                    errorMessage = "Clone not found"
                    onLaunchComplete(false)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading clone info")
                errorMessage = "Error loading clone: ${e.message}"
                onLaunchComplete(false)
            }
        }
        
        // Launch the app
        LaunchedEffect(cloneInfo) {
            cloneInfo?.let {
                try {
                    // Simulate launching the app (this would be a real launch in production)
                    delay(1500) // Simulate some loading time
                    
                    // In a real implementation, we would actually launch the app here
                    // For now, we'll just simulate a successful launch
                    val launchResult = true
                    
                    if (launchResult) {
                        // Update the launch time
                        cloneRepository.updateCloneRunningState(cloneId, true)
                        
                        // This proxy activity should finish when the real app launches
                        delay(500) // Give some time for the UI to show
                        onLaunchComplete(true)
                        finish()
                    } else {
                        errorMessage = "Failed to launch the app"
                        onLaunchComplete(false)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error launching app")
                    errorMessage = "Error launching app: ${e.message}"
                    onLaunchComplete(false)
                } finally {
                    isLoading = false
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Launching ${cloneInfo?.cloneName ?: "app"}...",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    
    companion object {
        /**
         * Create an intent to launch a cloned app
         */
        fun createLaunchIntent(context: Context, cloneId: String, packageName: String): Intent {
            return Intent(context, CloneProxyActivity::class.java).apply {
                putExtra(VirtualAppEngine.EXTRA_CLONE_ID, cloneId)
                putExtra(VirtualAppEngine.EXTRA_PACKAGE_NAME, packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }
}