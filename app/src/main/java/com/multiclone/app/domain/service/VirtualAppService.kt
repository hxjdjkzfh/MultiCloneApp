package com.multiclone.app.domain.service

import android.content.Context
import android.content.Intent
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.core.virtualization.CloneProxyActivity
import com.multiclone.app.core.virtualization.VirtualAppEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for managing virtual app environments and operations
 */
@Singleton
class VirtualAppService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val virtualAppEngine: VirtualAppEngine
) {
    /**
     * Create a new virtual environment for an app
     * @param packageName the package name of the app to virtualize
     * @return the ID of the created virtual environment
     */
    suspend fun createVirtualEnvironment(packageName: String): String {
        // Generate a unique ID for this virtual environment
        val virtualEnvId = "ve_${System.currentTimeMillis()}_${packageName.hashCode()}"
        
        // Initialize the virtual environment
        virtualAppEngine.setupVirtualEnvironment(virtualEnvId, packageName)
        
        // Start the background service to manage this clone if needed
        ensureServiceRunning()
        
        return virtualEnvId
    }
    
    /**
     * Launch an app in a virtual environment
     * @param packageName the package name of the app to launch
     * @param virtualEnvId the ID of the virtual environment
     */
    fun launchApp(packageName: String, virtualEnvId: String) {
        // Create an intent to launch the proxy activity
        val intent = Intent(context, CloneProxyActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(CloneProxyActivity.EXTRA_PACKAGE_NAME, packageName)
            putExtra(CloneProxyActivity.EXTRA_VIRTUAL_ENV_ID, virtualEnvId)
        }
        
        // Launch the activity
        context.startActivity(intent)
    }
    
    /**
     * Delete a virtual environment
     * @param virtualEnvId the ID of the virtual environment to delete
     */
    suspend fun deleteVirtualEnvironment(virtualEnvId: String) {
        virtualAppEngine.cleanupVirtualEnvironment(virtualEnvId)
    }
    
    /**
     * Ensure the clone manager service is running
     */
    private fun ensureServiceRunning() {
        val serviceIntent = Intent(context, CloneManagerService::class.java)
        context.startService(serviceIntent)
    }
}