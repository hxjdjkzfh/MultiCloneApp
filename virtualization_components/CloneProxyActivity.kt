package com.multiclone.app.virtualization

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.multiclone.app.domain.models.ClonedApp
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Proxy activity that serves as the entry point for launching cloned apps.
 * Acts as a bridge between the MultiClone app and the virtualized app instance.
 */
@AndroidEntryPoint
class CloneProxyActivity : Activity() {
    
    @Inject
    lateinit var cloneManager: CloneManager
    
    @Inject
    lateinit var storageManager: VirtualStorageManager
    
    @Inject
    lateinit var virtualizationService: VirtualizationService
    
    private var cloneId: String? = null
    private var packageName: String? = null
    private var cloneApp: ClonedApp? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("CloneProxyActivity created")
        
        // Get clone info from intent
        cloneId = intent.getStringExtra(VirtualAppEngine.EXTRA_CLONE_ID)
        packageName = intent.getStringExtra(VirtualAppEngine.EXTRA_PACKAGE_NAME)
        
        // Validate inputs
        if (cloneId.isNullOrBlank() || packageName.isNullOrBlank()) {
            Timber.e("Missing required clone information")
            showError("Missing clone information")
            finish()
            return
        }
        
        // Retrieve clone data
        cloneApp = cloneManager.getClone(cloneId!!)
        if (cloneApp == null) {
            Timber.e("Clone not found: $cloneId")
            showError("Clone not found")
            finish()
            return
        }
        
        // Ensure storage is prepared
        if (cloneApp!!.storageIsolated) {
            prepareVirtualStorage()
        }
        
        // Start the clone manager service
        startCloneManagerService()
        
        // Launch the original app with virtualization
        launchVirtualizedApp()
    }
    
    private fun prepareVirtualStorage() {
        // This would be a more complex implementation in a real app
        val cloneDir = storageManager.getCloneDirectory(packageName!!, cloneId!!)
        if (!cloneDir.exists()) {
            Timber.w("Clone directory doesn't exist, creating it now")
            cloneDir.mkdirs()
        }
    }
    
    private fun startCloneManagerService() {
        CloneManagerService.startService(this, cloneId!!)
    }
    
    private fun launchVirtualizedApp() {
        try {
            // Get the launch intent for the target app
            val launchIntent = getLaunchIntentForPackage(packageName!!)
            if (launchIntent == null) {
                Timber.e("No launch intent found for $packageName")
                showError("Cannot launch this app")
                finish()
                return
            }
            
            // Start the virtualization service
            val virtualIntent = Intent(this, VirtualizationService::class.java).apply {
                action = VirtualizationService.ACTION_PREPARE_ENVIRONMENT
                putExtra(VirtualizationService.EXTRA_CLONE_ID, cloneId)
                putExtra(VirtualizationService.EXTRA_PACKAGE_NAME, packageName)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(virtualIntent)
            } else {
                startService(virtualIntent)
            }
            
            // Modify the intent to work with our virtualization
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            // Add our identity so the virtualization service can identify this launch
            launchIntent.putExtra(VirtualizationService.EXTRA_VIRTUALIZED, true)
            launchIntent.putExtra(VirtualizationService.EXTRA_CLONE_ID, cloneId)
            
            // Start the target app
            startActivity(launchIntent)
            
            // We're done with this proxy activity
            finish()
        } catch (e: Exception) {
            Timber.e(e, "Error launching virtualized app")
            showError("Failed to launch app: ${e.message}")
            finish()
        }
    }
    
    /**
     * Get the launch intent for a package
     */
    private fun getLaunchIntentForPackage(packageName: String): Intent? {
        val packageManager = packageManager
        
        try {
            // Try to get the main activity through the package manager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            }
            
            val mainActivity = packageInfo.activities?.firstOrNull { activity ->
                // Find the activity with launcher intent filter
                val intentInfo = packageManager.getActivityInfo(
                    ComponentName(packageName, activity.name),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PackageManager.ComponentInfoFlags.of(0)
                    } else {
                        0
                    }
                )
                intentInfo.exported
            }
            
            if (mainActivity != null) {
                return Intent(Intent.ACTION_MAIN).apply {
                    component = ComponentName(packageName, mainActivity.name)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
            }
            
            // Fallback to the standard method
            return packageManager.getLaunchIntentForPackage(packageName)
        } catch (e: Exception) {
            Timber.e(e, "Error getting launch intent for $packageName")
            return null
        }
    }
    
    /**
     * Show an error message
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}