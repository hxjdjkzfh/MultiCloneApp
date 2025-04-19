package com.multiclone.app.core.virtualization

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages virtual environments for cloned applications.
 * Responsible for creating, configuring, and deleting the isolated
 * filesystem environments for each app clone.
 */
@Singleton
class CloneEnvironment @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val ENVIRONMENTS_DIR = "clone_environments"
        private const val DATA_DIR = "data"
        private const val STORAGE_DIR = "storage"
        private const val CONFIG_DIR = "config"
    }
    
    /**
     * Creates a new virtual environment for a cloned app
     * 
     * @param cloneId Unique identifier for the clone
     * @param packageName Package name of the original app
     * @return Success status of the operation
     */
    suspend fun createEnvironment(cloneId: String, packageName: String): Boolean = 
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Creating environment for clone $cloneId ($packageName)")
                
                // Get base directory for all environments
                val baseDir = getBaseEnvironmentsDirectory()
                if (!baseDir.exists() && !baseDir.mkdirs()) {
                    Timber.e("Failed to create base environments directory")
                    return@withContext false
                }
                
                // Create environment directory structure
                val envDir = File(baseDir, cloneId)
                if (envDir.exists()) {
                    Timber.w("Environment directory already exists for $cloneId, deleting first")
                    if (!envDir.deleteRecursively()) {
                        Timber.e("Failed to delete existing environment directory")
                        return@withContext false
                    }
                }
                
                // Create main directories
                if (!envDir.mkdirs()) {
                    Timber.e("Failed to create environment directory")
                    return@withContext false
                }
                
                // Create subdirectories
                val dataDir = File(envDir, DATA_DIR)
                val storageDir = File(envDir, STORAGE_DIR)
                val configDir = File(envDir, CONFIG_DIR)
                
                if (!dataDir.mkdirs() || !storageDir.mkdirs() || !configDir.mkdirs()) {
                    Timber.e("Failed to create environment subdirectories")
                    envDir.deleteRecursively()
                    return@withContext false
                }
                
                // Create package specific subdirectories
                val packageDataDir = File(dataDir, packageName)
                if (!packageDataDir.mkdirs()) {
                    Timber.e("Failed to create package data directory")
                    envDir.deleteRecursively()
                    return@withContext false
                }
                
                // Create configuration file
                val configFile = File(configDir, "environment.json")
                configFile.writeText("""
                    {
                        "cloneId": "$cloneId",
                        "packageName": "$packageName",
                        "created": ${System.currentTimeMillis()},
                        "version": 1
                    }
                """.trimIndent())
                
                Timber.d("Successfully created environment for clone $cloneId")
                return@withContext true
            } catch (e: Exception) {
                Timber.e(e, "Error creating environment for clone $cloneId")
                // Attempt cleanup on failure
                try {
                    val baseDir = getBaseEnvironmentsDirectory()
                    val envDir = File(baseDir, cloneId)
                    if (envDir.exists()) {
                        envDir.deleteRecursively()
                    }
                } catch (cleanupError: Exception) {
                    Timber.e(cleanupError, "Error during cleanup after failed environment creation")
                }
                return@withContext false
            }
        }
    
    /**
     * Deletes a virtual environment for a cloned app
     * 
     * @param cloneId Unique identifier for the clone to delete
     * @return Success status of the operation
     */
    suspend fun deleteEnvironment(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Deleting environment for clone $cloneId")
            
            val envDir = File(getBaseEnvironmentsDirectory(), cloneId)
            if (!envDir.exists()) {
                Timber.w("Environment directory doesn't exist for $cloneId")
                return@withContext true // Already deleted
            }
            
            if (!envDir.deleteRecursively()) {
                Timber.e("Failed to delete environment directory for $cloneId")
                return@withContext false
            }
            
            Timber.d("Successfully deleted environment for clone $cloneId")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error deleting environment for clone $cloneId")
            return@withContext false
        }
    }
    
    /**
     * Gets the directory for a specific clone environment
     * 
     * @param cloneId Unique identifier for the clone
     * @return The environment directory
     */
    fun getEnvironmentDirectory(cloneId: String): File {
        return File(getBaseEnvironmentsDirectory(), cloneId)
    }
    
    /**
     * Gets the data directory within a clone's environment
     * 
     * @param cloneId Unique identifier for the clone
     * @return The data directory for the clone
     */
    fun getDataDirectory(cloneId: String): File {
        return File(getEnvironmentDirectory(cloneId), DATA_DIR)
    }
    
    /**
     * Gets the storage directory within a clone's environment
     * 
     * @param cloneId Unique identifier for the clone
     * @return The storage directory for the clone
     */
    fun getStorageDirectory(cloneId: String): File {
        return File(getEnvironmentDirectory(cloneId), STORAGE_DIR)
    }
    
    /**
     * Gets the base directory for all environments
     * 
     * @return The base environments directory
     */
    private fun getBaseEnvironmentsDirectory(): File {
        // Use app's files directory rather than external storage
        // for better security and to avoid storage permission issues
        return File(context.filesDir, ENVIRONMENTS_DIR)
    }
}