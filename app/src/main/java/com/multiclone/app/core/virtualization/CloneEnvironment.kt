package com.multiclone.app.core.virtualization

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the virtual environment for cloned apps
 * This class is responsible for creating isolated directories
 * and managing resources for each cloned app instance
 */
@Singleton
class CloneEnvironment @Inject constructor(
    private val context: Context
) {
    private val TAG = "CloneEnvironment"
    
    // Base directory for all cloned apps
    private val baseCloneDir by lazy {
        File(context.filesDir, "clones").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * Prepare the environment for a new app clone
     * Creates necessary directories and initializes files
     * 
     * @param packageName The package name of the app to clone
     * @param cloneIndex The index of this clone (for multiple clones of the same app)
     * @return True if preparation was successful, false otherwise
     */
    suspend fun prepareEnvironment(packageName: String, cloneIndex: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            // Create the clone-specific directory
            val cloneDir = getCloneDir(packageName, cloneIndex)
            if (!cloneDir.exists()) {
                cloneDir.mkdirs()
            }
            
            // Create subdirectories
            createSubDirectories(cloneDir)
            
            // Create needed configuration files
            createConfigFiles(packageName, cloneIndex, cloneDir)
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing environment", e)
            false
        }
    }
    
    /**
     * Clean up the environment for a deleted clone
     * 
     * @param packageName The package name of the app
     * @param cloneIndex The index of the clone to clean up
     * @return True if cleanup was successful, false otherwise
     */
    suspend fun cleanupEnvironment(packageName: String, cloneIndex: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val cloneDir = getCloneDir(packageName, cloneIndex)
            if (cloneDir.exists()) {
                deleteRecursively(cloneDir)
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up environment", e)
            false
        }
    }
    
    /**
     * Get the directory for a specific clone
     */
    fun getCloneDir(packageName: String, cloneIndex: Int): File {
        return File(baseCloneDir, "${packageName}_$cloneIndex")
    }
    
    /**
     * Get the data directory for a specific clone
     */
    fun getCloneDataDir(packageName: String, cloneIndex: Int): File {
        return File(getCloneDir(packageName, cloneIndex), "data")
    }
    
    /**
     * Create the necessary subdirectories for a cloned app
     */
    private fun createSubDirectories(cloneDir: File) {
        // Data directory for app data
        File(cloneDir, "data").mkdirs()
        
        // Cache directory
        File(cloneDir, "cache").mkdirs()
        
        // Shared preferences directory
        File(cloneDir, "shared_prefs").mkdirs()
        
        // Database directory
        File(cloneDir, "databases").mkdirs()
        
        // Files directory
        File(cloneDir, "files").mkdirs()
        
        // Create external storage directories if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File(cloneDir, "external_files").mkdirs()
        }
    }
    
    /**
     * Create configuration files for the cloned app
     */
    private fun createConfigFiles(packageName: String, cloneIndex: Int, cloneDir: File) {
        // Create a basic configuration file
        val configFile = File(cloneDir, "clone_config.json")
        val configContent = """
            {
              "packageName": "$packageName",
              "cloneIndex": $cloneIndex,
              "createdAt": ${System.currentTimeMillis()},
              "androidVersion": "${Build.VERSION.RELEASE}",
              "sdkVersion": ${Build.VERSION.SDK_INT}
            }
        """.trimIndent()
        
        configFile.writeText(configContent)
        
        // Create a mapping file for resource redirection
        val mappingFile = File(cloneDir, "resource_mapping.json")
        val mappingContent = """
            {
              "redirects": [
                {"original": "/data/data/$packageName", "redirect": "${cloneDir.absolutePath}/data"},
                {"original": "/data/user/0/$packageName", "redirect": "${cloneDir.absolutePath}/data"}
              ]
            }
        """.trimIndent()
        
        mappingFile.writeText(mappingContent)
    }
    
    /**
     * Recursively delete a directory and all its contents
     */
    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteRecursively(child)
            }
        }
        return file.delete()
    }
}