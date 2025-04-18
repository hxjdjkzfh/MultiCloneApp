package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages active virtual app sessions and lifecycle events
 */
@Singleton
class VirtualAppManager @Inject constructor(
    private val context: Context,
    private val cloneRepository: CloneRepository
) : LifecycleEventObserver {
    
    // Map of active clone environments (cloneId -> environment)
    private val activeEnvironments = mutableMapOf<String, CloneEnvironment>()
    
    // Last active clone ID
    private var lastActiveCloneId: String? = null
    
    init {
        // Register for app lifecycle events
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    
    /**
     * Prepares a clone for launch by setting up its virtual environment
     */
    fun prepareCloneForLaunch(packageName: String, cloneId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Get the clone from repository
            val clone = cloneRepository.getCloneById(cloneId)
            
            if (clone != null) {
                // Create or get the environment
                val environment = getEnvironment(clone)
                
                // Prepare the environment for launch
                environment.prepare()
                
                // Store as the active clone
                lastActiveCloneId = cloneId
                activeEnvironments[cloneId] = environment
            }
        }
    }
    
    /**
     * Retrieve or create a virtual environment for a clone
     */
    private fun getEnvironment(clone: CloneInfo): CloneEnvironment {
        // Check if we already have an active environment for this clone
        activeEnvironments[clone.id]?.let { return it }
        
        // Create a new environment
        return CloneEnvironment(
            context = context,
            cloneId = clone.id,
            packageName = clone.packageName,
            displayName = clone.displayName
        )
    }
    
    /**
     * Start the background service for virtualization support
     */
    fun startVirtualizationService() {
        val serviceIntent = Intent(context, VirtualizationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
    
    /**
     * Stop the background service
     */
    fun stopVirtualizationService() {
        val serviceIntent = Intent(context, VirtualizationService::class.java)
        context.stopService(serviceIntent)
    }
    
    /**
     * Handle app lifecycle events
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                // App has come to foreground
                startVirtualizationService()
            }
            Lifecycle.Event.ON_STOP -> {
                // App has gone to background, clean up resources if needed
                CoroutineScope(Dispatchers.IO).launch {
                    cleanupInactiveEnvironments()
                }
            }
            else -> { /* Ignore other events */ }
        }
    }
    
    /**
     * Clean up inactive environments to free resources
     */
    private suspend fun cleanupInactiveEnvironments() = withContext(Dispatchers.IO) {
        // Keep the last active environment (could be still transitioning)
        val lastActive = lastActiveCloneId
        
        // Clean up other environments
        activeEnvironments.entries
            .filter { it.key != lastActive }
            .forEach { (id, env) ->
                env.cleanup()
                activeEnvironments.remove(id)
            }
    }
}