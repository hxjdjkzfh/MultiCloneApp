package com.multiclone.app.core.virtualization

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Proxy activity that redirects to the real app after setting up the virtual environment
 */
@AndroidEntryPoint
class CloneProxyActivity : Activity() {
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var virtualAppManager: VirtualAppManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the package name and clone ID from intent
        val packageName = intent.getStringExtra("packageName") ?: run {
            finish()
            return
        }
        
        val cloneId = intent.getStringExtra("cloneId") ?: run {
            finish()
            return
        }
        
        // Update last used timestamp
        CoroutineScope(Dispatchers.IO).launch {
            cloneRepository.updateLastUsedTime(cloneId, System.currentTimeMillis())
        }
        
        // Prepare the virtual environment
        setupVirtualEnvironment(packageName, cloneId)
        
        // Launch the real app
        launchOriginalApp(packageName)
    }
    
    private fun setupVirtualEnvironment(packageName: String, cloneId: String) {
        // Register the clone with the virtual app manager
        virtualAppManager.prepareCloneForLaunch(packageName, cloneId)
    }
    
    private fun launchOriginalApp(packageName: String) {
        // Find the main activity of the target app
        val intent = getLaunchIntentForPackage(packageName)
        
        if (intent != null) {
            // Add flags to create a new task
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            // Add our custom data to identify this as a cloned instance
            intent.putExtra("com.multiclone.IS_CLONE", true)
            
            // Start the activity
            startActivity(intent)
        }
        
        // Finish this proxy activity
        finish()
    }
    
    private fun getLaunchIntentForPackage(packageName: String): Intent? {
        // Get the package manager
        val pm = packageManager
        
        // Try to get the launch intent for the package
        return try {
            // Get all activities with LAUNCHER category
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(packageName)
            
            val resolveInfoList = pm.queryIntentActivities(intent, 0)
            
            if (resolveInfoList.isNotEmpty()) {
                val resolveInfo = resolveInfoList[0]
                val activityInfo = resolveInfo.activityInfo
                
                // Create an intent to launch the specific activity
                Intent().apply {
                    component = ComponentName(
                        activityInfo.applicationInfo.packageName,
                        activityInfo.name
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}