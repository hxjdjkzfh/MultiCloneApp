package com.multiclone.app.core.virtualization

import android.content.Context
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core engine for app virtualization
 * 
 * Manages the virtualization of apps and provides an environment for clones to run in
 */
@Singleton
class VirtualAppEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneRepository: CloneRepository
) {
    
    // Map of active clone environments
    private val activeEnvironments = mutableMapOf<String, CloneEnvironment>()
    
    /**
     * Initialize or retrieve the environment for a cloned app
     */
    suspend fun initializeCloneEnvironment(cloneId: String, packageName: String): CloneEnvironment {
        // Check if environment is already active
        val existing = activeEnvironments[cloneId]
        if (existing != null) {
            return existing
        }
        
        // Create a new environment
        return withContext(Dispatchers.IO) {
            // Get clone info from repository
            val clone = cloneRepository.clones.collect { clones ->
                clones.find { it.id == cloneId }
            }
            
            // Get or create clone directory
            val cloneDir = if (clone != null && clone.storagePath.isNotEmpty()) {
                File(clone.storagePath)
            } else {
                File(context.filesDir, "${ClonedAppInstaller.CLONES_DIRECTORY}/$cloneId")
            }
            
            if (!cloneDir.exists()) {
                cloneDir.mkdirs()
            }
            
            // Create environment
            val environment = CloneEnvironment(
                context = context,
                cloneId = cloneId,
                packageName = packageName,
                storageDir = cloneDir
            )
            
            // Store in active environments
            activeEnvironments[cloneId] = environment
            
            // Update last used time
            cloneRepository.updateLastUsedTime(cloneId)
            
            return@withContext environment
        }
    }
    
    /**
     * Remove a clone environment from active environments
     */
    fun releaseEnvironment(cloneId: String) {
        activeEnvironments.remove(cloneId)
    }
    
    /**
     * Get all active environments
     */
    fun getActiveEnvironments(): Map<String, CloneEnvironment> {
        return activeEnvironments.toMap()
    }
    
    /**
     * Check if a clone environment is active
     */
    fun isEnvironmentActive(cloneId: String): Boolean {
        return activeEnvironments.containsKey(cloneId)
    }
}