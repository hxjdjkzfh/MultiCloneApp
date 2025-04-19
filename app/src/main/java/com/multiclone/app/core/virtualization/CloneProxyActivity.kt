package com.multiclone.app.core.virtualization

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Proxy activity that intercepts app launch intents and redirects them
 * to the appropriate cloned app environment.
 * 
 * This activity serves as a bridge between the Android system and our
 * virtualized app environments.
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    
    @Inject
    lateinit var virtualizationService: VirtualizationService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get clone ID from intent
        val cloneId = intent.getStringExtra(VirtualAppEngine.CLONE_INTENT_EXTRA)
        if (cloneId.isNullOrEmpty()) {
            Timber.e("No clone ID provided in intent")
            showErrorAndFinish("Failed to launch app: Missing clone ID")
            return
        }
        
        // Get original package name
        val packageName = intent.getStringExtra("original_package")
        if (packageName.isNullOrEmpty()) {
            Timber.e("No package name provided in intent")
            showErrorAndFinish("Failed to launch app: Missing package name")
            return
        }
        
        // Get original intent if any
        val originalIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("original_intent", Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("original_intent")
        }
        
        Timber.d("Launching clone $cloneId for app $packageName")
        
        // Launch the cloned app
        launchClonedApp(cloneId, packageName, originalIntent)
    }
    
    /**
     * Launches a cloned app with the specified parameters
     */
    private fun launchClonedApp(cloneId: String, packageName: String, originalIntent: Intent?) {
        try {
            // Notify the virtualization service to prepare the environment
            virtualizationService.prepareEnvironment(cloneId, packageName)
            
            // Create a modified intent that will launch the actual app
            // but in our virtualized environment
            val launchIntent = originalIntent?.clone() as? Intent 
                ?: packageManager.getLaunchIntentForPackage(packageName)?.clone() as? Intent
            
            if (launchIntent == null) {
                Timber.e("Failed to create launch intent for $packageName")
                showErrorAndFinish("Failed to launch app: Could not create launch intent")
                return
            }
            
            // Modify the intent to include our virtualization parameters
            launchIntent.apply {
                putExtra("__VIRTUALIZED__", true)
                putExtra("__CLONE_ID__", cloneId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Start the cloned app
            Timber.d("Starting cloned app with intent: $launchIntent")
            virtualizationService.launchApp(launchIntent, cloneId, packageName)
            
            // Our work is done, finish this activity
            finish()
        } catch (e: Exception) {
            Timber.e(e, "Error launching cloned app $packageName (clone ID: $cloneId)")
            showErrorAndFinish("Failed to launch app: ${e.message}")
        }
    }
    
    /**
     * Shows an error message and finishes the activity
     */
    private fun showErrorAndFinish(message: String) {
        // In a real app, we'd show a proper error UI
        // For now, we just log the error and finish
        Timber.e(message)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
    
    companion object {
        /**
         * Creates an intent to launch a specific cloned app
         * 
         * @param context Context to create the intent with
         * @param cloneId The ID of the clone to launch
         * @param packageName The original app's package name
         * @return Intent to launch the cloned app
         */
        fun createLaunchIntent(context: Context, cloneId: String, packageName: String): Intent {
            return Intent(context, CloneProxyActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                putExtra(VirtualAppEngine.CLONE_INTENT_EXTRA, cloneId)
                putExtra("original_package", packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}