package com.multiclone.app.core.virtualization

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A proxy activity that handles launching cloned apps
 * This activity receives a clone ID, loads its virtual environment,
 * then launches the actual app inside that environment
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {

    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val packageName = intent.getStringExtra("packageName")
        val cloneId = intent.getStringExtra("cloneId")
        val displayName = intent.getStringExtra("displayName")
        
        if (packageName == null || cloneId == null) {
            Toast.makeText(this, "Error: Missing package information", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        launchClonedApp(packageName, cloneId, displayName ?: packageName)
    }
    
    private fun launchClonedApp(packageName: String, cloneId: String, displayName: String) {
        lifecycleScope.launch {
            try {
                // Setup virtual environment
                withContext(Dispatchers.IO) {
                    // Ensure the CloneManagerService is running
                    startService(Intent(this@CloneProxyActivity, CloneManagerService::class.java))
                    
                    // Prepare the environment - in a real app, this would set up file redirection,
                    // permission isolation, etc.
                }
                
                // Launch the actual application
                val launchIntent = getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    // In a real implementation, we would modify the intent to use our virtualization layer
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    
                    // Add environment info
                    launchIntent.putExtra("VIRTUAL_ENV_ID", cloneId)
                    launchIntent.putExtra("CLONE_DISPLAY_NAME", displayName)
                    
                    startActivity(launchIntent)
                } else {
                    Toast.makeText(
                        this@CloneProxyActivity,
                        "Unable to launch $displayName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CloneProxyActivity, 
                        "Error launching app: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                // Always finish this proxy activity
                finish()
            }
        }
    }
    
    private fun getLaunchIntentForPackage(packageName: String): Intent? {
        return try {
            packageManager.getLaunchIntentForPackage(packageName)
        } catch (e: Exception) {
            null
        }
    }
}