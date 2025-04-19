package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core engine that manages virtual app environments
 */
@Singleton
class VirtualAppEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneRepository: CloneRepository,
    private val cloneEnvironment: CloneEnvironment,
    private val cloneManagerService: CloneManagerService
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    
    /**
     * Initialize the engine
     */
    fun initialize() {
        // Start the manager service
        cloneManagerService.startService()
        
        // Initialize virtualization components
        cloneEnvironment.initialize()
    }
    
    /**
     * Launch a cloned app
     */
    fun launchClone(packageName: String, cloneId: String) {
        // Update usage timestamp
        scope.launch {
            cloneRepository.updateLastUsedTime(cloneId)
        }
        
        // Start proxy activity to launch the clone
        val intent = Intent(context, CloneProxyActivity::class.java).apply {
            putExtra(CloneProxyActivity.EXTRA_PACKAGE_NAME, packageName)
            putExtra(CloneProxyActivity.EXTRA_CLONE_ID, cloneId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    /**
     * Check if a package can be cloned
     */
    fun canClonePackage(packageName: String): Boolean {
        // Maintain a blocklist of apps that cannot be cloned
        val blockedPackages = listOf(
            "com.android.systemui",
            "com.google.android.permissioncontroller",
            "com.android.settings",
            context.packageName // Don't allow cloning of our own app
        )
        
        // Check if the package is in the blocklist
        if (blockedPackages.contains(packageName)) {
            return false
        }
        
        // Additional checks can be added here, for example:
        // - Check if the app requires system permissions that can't be virtualized
        // - Check if the app is already running in a virtual environment
        
        return true
    }
    
    /**
     * Clean up resources when the app is being destroyed
     */
    fun shutdown() {
        cloneManagerService.stopService()
    }
}