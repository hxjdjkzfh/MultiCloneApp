package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing cloned apps and installed apps
 */
@Singleton
class CloneRepository @Inject constructor(
    private val context: Context,
    private val json: Json
) {
    private val clonesDir = File(context.filesDir, "clones")
    private val runningClones = mutableSetOf<String>()
    
    init {
        // Ensure the clones directory exists
        if (!clonesDir.exists()) {
            clonesDir.mkdirs()
        }
    }
    
    /**
     * Get all installed apps on the device
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedApps = mutableListOf<AppInfo>()
        
        try {
            val packages = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledPackages(0)
            }
            
            Timber.d("Found ${packages.size} installed packages")
            
            packages.forEach { packageInfo ->
                val appInfo = packageInfo.applicationInfo
                
                // Skip system apps (optional filter)
                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                
                // Skip our own app
                if (packageInfo.packageName != context.packageName) {
                    val appName = appInfo.loadLabel(packageManager).toString()
                    val versionName = packageInfo.versionName ?: "Unknown"
                    val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode.toLong()
                    }
                    
                    // Get app icon bitmap
                    val iconDrawable = appInfo.loadIcon(packageManager)
                    val iconBitmap = iconDrawable.toBitmap()
                    
                    installedApps.add(
                        AppInfo(
                            packageName = packageInfo.packageName,
                            appName = appName,
                            versionName = versionName,
                            versionCode = versionCode,
                            isSystemApp = isSystemApp,
                            appIcon = iconBitmap
                        )
                    )
                }
            }
            
            // Sort by app name
            return@withContext installedApps.sortedBy { it.appName }
        } catch (e: Exception) {
            Timber.e(e, "Error getting installed apps")
            return@withContext emptyList<AppInfo>()
        }
    }
    
    /**
     * Save a clone
     */
    suspend fun saveClone(clone: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val cloneFile = File(clonesDir, "${clone.id}.json")
            val cloneJson = json.encodeToString(clone)
            cloneFile.writeText(cloneJson)
            Timber.d("Saved clone: ${clone.id}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error saving clone: ${clone.id}")
            return@withContext false
        }
    }
    
    /**
     * Get all clones
     */
    suspend fun getAllClones(): List<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            val cloneFiles = clonesDir.listFiles() ?: return@withContext emptyList()
            Timber.d("Found ${cloneFiles.size} clone files")
            
            val clones = cloneFiles
                .filter { it.extension == "json" }
                .map { file ->
                    try {
                        val cloneJson = file.readText()
                        val clone = json.decodeFromString<CloneInfo>(cloneJson)
                        
                        // Set the running state based on our tracking
                        if (runningClones.contains(clone.id)) {
                            clone.copy(isRunning = true)
                        } else {
                            clone
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing clone file: ${file.name}")
                        null
                    }
                }
                .filterNotNull()
                .sortedByDescending { it.lastUsedAt }
            
            Timber.d("Loaded ${clones.size} clones")
            return@withContext clones
        } catch (e: Exception) {
            Timber.e(e, "Error getting all clones")
            return@withContext emptyList()
        }
    }
    
    /**
     * Get a clone by ID
     */
    suspend fun getCloneById(id: String): CloneInfo? = withContext(Dispatchers.IO) {
        try {
            val cloneFile = File(clonesDir, "$id.json")
            if (!cloneFile.exists()) {
                Timber.d("Clone not found: $id")
                return@withContext null
            }
            
            val cloneJson = cloneFile.readText()
            val clone = json.decodeFromString<CloneInfo>(cloneJson)
            
            // Set the running state based on our tracking
            if (runningClones.contains(clone.id)) {
                clone.copy(isRunning = true)
            } else {
                clone
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting clone: $id")
            null
        }
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cloneFile = File(clonesDir, "$id.json")
            if (!cloneFile.exists()) {
                Timber.d("Clone not found for deletion: $id")
                return@withContext false
            }
            
            // Remove from running clones if needed
            runningClones.remove(id)
            
            val result = cloneFile.delete()
            Timber.d("Deleted clone $id: $result")
            return@withContext result
        } catch (e: Exception) {
            Timber.e(e, "Error deleting clone: $id")
            return@withContext false
        }
    }
    
    /**
     * Update the running state of a clone
     */
    suspend fun updateCloneRunningState(id: String, isRunning: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isRunning) {
                runningClones.add(id)
            } else {
                runningClones.remove(id)
            }
            
            // Also update the lastUsedAt time if it's being launched
            if (isRunning) {
                getCloneById(id)?.let { clone ->
                    val updatedClone = clone.copy(
                        lastUsedAt = System.currentTimeMillis(),
                        isRunning = true
                    )
                    saveClone(updatedClone)
                }
            }
            
            Timber.d("Updated running state for clone $id to $isRunning")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error updating running state for clone: $id")
            return@withContext false
        }
    }
    
    /**
     * Get the number of running clones
     */
    suspend fun getRunningCloneCount(): Int = withContext(Dispatchers.IO) {
        return@withContext runningClones.size
    }
    
    /**
     * Check if a clone is running
     */
    suspend fun isCloneRunning(id: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext runningClones.contains(id)
    }
    
    /**
     * Get the number of clones for a package
     */
    fun getCloneCountForPackage(packageName: String): Int {
        var count = 0
        
        // For now, we'll do a simple scan of the clones directory
        clonesDir.listFiles()?.filter { it.extension == "json" }?.forEach { file ->
            try {
                val cloneJson = file.readText()
                val clone = json.decodeFromString<CloneInfo>(cloneJson)
                if (clone.packageName == packageName) {
                    count++
                }
            } catch (e: Exception) {
                // Ignore parsing errors
            }
        }
        
        return count
    }
}