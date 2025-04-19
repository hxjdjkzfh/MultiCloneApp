package com.multiclone.app.core.virtualization

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.multiclone.app.R
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Activity for proxying cloned app launches.
 * This is the entry point from launcher shortcuts and URI scheme handlers.
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {

    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the clone ID from the intent
        val cloneId = intent.getStringExtra("clone_id")
        
        if (cloneId.isNullOrEmpty()) {
            // No clone ID provided, handle URI scheme
            handleUriScheme()
        } else {
            // Launch the cloned app
            launchClonedApp(cloneId)
        }
    }
    
    /**
     * Launch a cloned app
     */
    private fun launchClonedApp(cloneId: String) {
        lifecycleScope.launch {
            try {
                Timber.d("Launching clone $cloneId")
                
                // Get the clone info
                val cloneInfo = cloneRepository.getCloneById(cloneId)
                
                if (cloneInfo == null) {
                    Timber.e("Clone not found: $cloneId")
                    showError(getString(R.string.error_app_not_found))
                    return@launch
                }
                
                // Update launch statistics
                cloneRepository.updateLaunchStats(cloneId)
                
                // Launch the app through the virtualization engine
                val launched = virtualAppEngine.launchApp(cloneInfo)
                
                if (!launched) {
                    Timber.e("Failed to launch clone $cloneId")
                    showError(getString(R.string.error_virtualization))
                }
                
                // Finish this activity
                finish()
            } catch (e: Exception) {
                Timber.e(e, "Error launching clone")
                showError(e.message ?: getString(R.string.error_virtualization))
            }
        }
    }
    
    /**
     * Handle deep links via the multiclone:// URI scheme
     */
    private fun handleUriScheme() {
        val uri = intent.data
        if (uri != null && uri.scheme == "multiclone") {
            try {
                when (uri.host) {
                    "launch" -> {
                        // Format: multiclone://launch/CLONE_ID
                        val cloneId = uri.pathSegments.firstOrNull()
                        if (cloneId != null) {
                            launchClonedApp(cloneId)
                        } else {
                            showError(getString(R.string.error_invalid_operation))
                        }
                    }
                    "create" -> {
                        // Format: multiclone://create/PACKAGE_NAME
                        val packageName = uri.pathSegments.firstOrNull()
                        if (packageName != null) {
                            // Redirect to the main app with the package name
                            val mainIntent = Intent(this, Class.forName("com.multiclone.app.MainActivity")).apply {
                                action = "com.multiclone.app.action.CREATE_CLONE"
                                putExtra("package_name", packageName)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(mainIntent)
                            finish()
                        } else {
                            showError(getString(R.string.error_invalid_operation))
                        }
                    }
                    else -> {
                        showError(getString(R.string.error_invalid_operation))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling URI scheme")
                showError(e.message ?: getString(R.string.error_invalid_operation))
            }
        } else {
            showError(getString(R.string.error_invalid_operation))
        }
    }
    
    /**
     * Show an error message
     */
    private fun showError(message: String) {
        // In a real implementation, we would show a dialog or toast
        Timber.e("Error: $message")
        
        // For now, just redirect to the main app with the error
        val mainIntent = Intent(this, Class.forName("com.multiclone.app.MainActivity")).apply {
            action = "com.multiclone.app.action.SHOW_ERROR"
            putExtra("error_message", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(mainIntent)
        finish()
    }
}