package com.multiclone.app.core.virtualization

import android.content.Context
import android.os.Environment
import com.multiclone.app.data.model.CloneInfo
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the isolated environment for app clones
 */
@Singleton
class CloneEnvironment @Inject constructor(
    private val context: Context
) {
    // Root directory for environment storage
    private val environmentRoot: File by lazy {
        File(context.filesDir, "environments")
    }
    
    init {
        // Ensure the environments directory exists
        if (!environmentRoot.exists()) {
            environmentRoot.mkdirs()
        }
    }
    
    /**
     * Set up the isolated environment for a clone
     */
    fun setupEnvironment(cloneInfo: CloneInfo): Boolean {
        try {
            Timber.d("Setting up environment for clone: ${cloneInfo.id}")
            
            // Create the environment directory
            val envDir = File(environmentRoot, cloneInfo.id)
            if (!envDir.exists()) {
                envDir.mkdirs()
            }
            
            // Create subdirectories for the clone's data
            createEnvironmentStructure(envDir)
            
            // Create a basic environment configuration file
            val configFile = File(envDir, "environment.json")
            val config = """
                {
                    "cloneId": "${cloneInfo.id}",
                    "originalPackage": "${cloneInfo.originalPackageName}",
                    "created": ${System.currentTimeMillis()},
                    "storage": {
                        "internal": "${envDir.absolutePath}/internal",
                        "external": "${envDir.absolutePath}/external"
                    }
                }
            """.trimIndent()
            
            configFile.writeText(config)
            
            Timber.d("Environment set up successfully for clone: ${cloneInfo.id}")
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to set up environment for clone: ${cloneInfo.id}")
            return false
        }
    }
    
    /**
     * Clean up the environment for a clone
     */
    fun cleanupEnvironment(cloneId: String): Boolean {
        try {
            Timber.d("Cleaning up environment for clone: $cloneId")
            
            val envDir = File(environmentRoot, cloneId)
            if (!envDir.exists()) {
                Timber.w("Environment directory doesn't exist for clone: $cloneId")
                return true
            }
            
            // Recursively delete the environment directory
            return envDir.deleteRecursively()
        } catch (e: Exception) {
            Timber.e(e, "Failed to clean up environment for clone: $cloneId")
            return false
        }
    }
    
    /**
     * Get the environment path for a clone
     */
    fun getEnvironmentPath(cloneId: String): String {
        return File(environmentRoot, cloneId).absolutePath
    }
    
    /**
     * Create the standard directory structure for an app environment
     */
    private fun createEnvironmentStructure(envDir: File) {
        // Create internal and external storage directories
        val internalDir = File(envDir, "internal")
        internalDir.mkdirs()
        
        val externalDir = File(envDir, "external")
        externalDir.mkdirs()
        
        // Create standard app directories
        val dataDirs = listOf(
            File(internalDir, "files"),
            File(internalDir, "cache"),
            File(internalDir, "shared_prefs"),
            File(internalDir, "databases"),
            File(externalDir, "Download"),
            File(externalDir, "Pictures"),
            File(externalDir, "Documents")
        )
        
        dataDirs.forEach { it.mkdirs() }
    }
    
    /**
     * Get the internal storage path for a clone
     */
    fun getInternalStoragePath(cloneId: String): String {
        return File(File(environmentRoot, cloneId), "internal").absolutePath
    }
    
    /**
     * Get the external storage path for a clone
     */
    fun getExternalStoragePath(cloneId: String): String {
        return File(File(environmentRoot, cloneId), "external").absolutePath
    }
}