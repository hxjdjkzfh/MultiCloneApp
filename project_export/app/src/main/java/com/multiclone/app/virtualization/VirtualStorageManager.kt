package com.multiclone.app.virtualization

import android.content.Context
import android.os.Build
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages isolated storage for cloned apps.
 * Creates, maintains, and cleans up storage areas for clone instances.
 */
@Singleton
class VirtualStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Base directory for all cloned app data
    private val baseDir: File by lazy {
        File(context.getExternalFilesDir(null), CLONES_BASE_DIR).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * Create isolated storage for a clone
     * @param packageName Original package name
     * @param cloneId Unique ID for the clone
     * @return True if storage was created successfully
     */
    suspend fun createIsolatedStorage(packageName: String, cloneId: String): Boolean = 
        withContext(Dispatchers.IO) {
            try {
                val cloneDir = getCloneDirectory(packageName, cloneId)
                
                // Create main directory
                if (!cloneDir.exists() && !cloneDir.mkdirs()) {
                    Timber.e("Failed to create directory: ${cloneDir.absolutePath}")
                    return@withContext false
                }
                
                // Create standard subdirectories
                val directories = ArrayList<File>().apply {
                    add(File(cloneDir, "files"))
                    add(File(cloneDir, "cache"))
                    add(File(cloneDir, "databases"))
                    add(File(cloneDir, "shared_prefs"))
                }
                
                // Create each directory
                directories.forEach { dir ->
                    if (!dir.exists() && !dir.mkdirs()) {
                        Timber.e("Failed to create directory: ${dir.absolutePath}")
                        return@withContext false
                    }
                }
                
                Timber.d("Created isolated storage for $packageName:$cloneId")
                true
            } catch (e: Exception) {
                Timber.e(e, "Error creating isolated storage for $packageName:$cloneId")
                false
            }
        }
    
    /**
     * Remove isolated storage for a clone
     * @param packageName Original package name
     * @param cloneId Unique ID for the clone
     * @return True if storage was removed successfully
     */
    suspend fun removeIsolatedStorage(packageName: String, cloneId: String): Boolean = 
        withContext(Dispatchers.IO) {
            try {
                val cloneDir = getCloneDirectory(packageName, cloneId)
                
                // Check if directory exists
                if (!cloneDir.exists()) {
                    Timber.d("Storage for $packageName:$cloneId already removed")
                    return@withContext true
                }
                
                // Delete directory recursively
                val result = deleteRecursively(cloneDir)
                
                Timber.d("Removed isolated storage for $packageName:$cloneId: $result")
                result
            } catch (e: Exception) {
                Timber.e(e, "Error removing isolated storage for $packageName:$cloneId")
                false
            }
        }
    
    /**
     * Get the base directory for a specific clone
     */
    fun getCloneDirectory(packageName: String, cloneId: String): File {
        return File(baseDir, "$packageName/$cloneId")
    }
    
    /**
     * Get a specific file in the clone's directory
     */
    fun getCloneFile(packageName: String, cloneId: String, relativePath: String): File {
        return File(getCloneDirectory(packageName, cloneId), relativePath)
    }
    
    /**
     * Clear cache for a specific clone
     */
    suspend fun clearCache(packageName: String, cloneId: String): Boolean = 
        withContext(Dispatchers.IO) {
            try {
                val cacheDir = File(getCloneDirectory(packageName, cloneId), "cache")
                if (cacheDir.exists()) {
                    deleteRecursively(cacheDir)
                    // Recreate the directory
                    cacheDir.mkdirs()
                }
                true
            } catch (e: Exception) {
                Timber.e(e, "Error clearing cache for $packageName:$cloneId")
                false
            }
        }
    
    /**
     * Get the storage usage for a clone in bytes
     */
    suspend fun getStorageUsage(packageName: String, cloneId: String): Long = 
        withContext(Dispatchers.IO) {
            try {
                val cloneDir = getCloneDirectory(packageName, cloneId)
                if (!cloneDir.exists()) {
                    return@withContext 0L
                }
                
                calculateDirectorySize(cloneDir)
            } catch (e: Exception) {
                Timber.e(e, "Error calculating storage usage for $packageName:$cloneId")
                0L
            }
        }
    
    /**
     * Recursively delete a directory and its contents
     */
    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteRecursively(child)
            }
        }
        return file.delete()
    }
    
    /**
     * Calculate the size of a directory and its contents
     */
    private fun calculateDirectorySize(directory: File): Long {
        var size = 0L
        
        directory.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateDirectorySize(file)
            } else {
                file.length()
            }
        }
        
        return size
    }
    
    companion object {
        private const val CLONES_BASE_DIR = "multiclone/clones"
    }
}