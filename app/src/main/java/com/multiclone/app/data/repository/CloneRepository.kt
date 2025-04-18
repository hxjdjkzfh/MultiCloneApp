package com.multiclone.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.content.edit
import com.multiclone.app.data.model.CloneInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to handle operations related to cloned applications
 */
@Singleton
class CloneRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "CloneRepository"
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "clone_manager_prefs",
        Context.MODE_PRIVATE
    )
    
    private val cloneIdsKey = "clone_ids"
    private val cloneDir = File(context.filesDir, "clones")
    
    init {
        // Ensure directory exists
        cloneDir.mkdirs()
    }
    
    /**
     * Get all clones
     */
    fun getAllClones(): Flow<List<CloneInfo>> = flow {
        val cloneIds = getCloneIds()
        val clones = cloneIds.mapNotNull { getCloneById(it) }
            .sortedByDescending { it.lastUsedTime }
        emit(clones)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get clones for a specific package
     */
    suspend fun getClonesByPackage(packageName: String): List<CloneInfo> = withContext(Dispatchers.IO) {
        val cloneIds = getCloneIds()
        cloneIds.mapNotNull { getCloneById(it) }
            .filter { it.packageName == packageName }
            .sortedByDescending { it.lastUsedTime }
    }
    
    /**
     * Get a specific clone by ID
     */
    suspend fun getCloneById(cloneId: String): CloneInfo? = withContext(Dispatchers.IO) {
        val packageName = prefs.getString("clone_${cloneId}_package", null) ?: return@withContext null
        val originalAppName = prefs.getString("clone_${cloneId}_original_name", "") ?: ""
        val cloneName = prefs.getString("clone_${cloneId}_name", "") ?: ""
        val creationTime = prefs.getLong("clone_${cloneId}_creation_time", 0L)
        val lastUsedTime = prefs.getLong("clone_${cloneId}_last_used", 0L)
        val hasShortcut = prefs.getBoolean("clone_${cloneId}_has_shortcut", false)
        val environmentId = prefs.getString("clone_${cloneId}_environment", cloneId) ?: cloneId
        val badgeNumber = prefs.getString("clone_${cloneId}_badge", "2")
        val launchCount = prefs.getInt("clone_${cloneId}_launch_count", 0)
        
        val customIcon = loadIconForClone(cloneId)
        
        CloneInfo(
            id = cloneId,
            packageName = packageName,
            originalAppName = originalAppName,
            cloneName = cloneName,
            customIcon = customIcon,
            creationTime = creationTime,
            lastUsedTime = lastUsedTime,
            hasShortcut = hasShortcut,
            environmentId = environmentId,
            badgeNumber = badgeNumber,
            launchCount = launchCount
        )
    }
    
    /**
     * Save a new clone
     */
    suspend fun saveClone(
        packageName: String,
        originalAppName: String,
        cloneName: String,
        customIcon: Bitmap? = null,
        badgeNumber: String? = "2",
        id: String = UUID.randomUUID().toString()
    ): CloneInfo = withContext(Dispatchers.IO) {
        Log.d(TAG, "Saving clone $id for package $packageName with name '$cloneName'")
        
        val cloneInfo = CloneInfo(
            id = id,
            packageName = packageName,
            originalAppName = originalAppName,
            cloneName = cloneName,
            customIcon = customIcon,
            creationTime = System.currentTimeMillis(),
            badgeNumber = badgeNumber
        )
        
        // Save clone info in SharedPreferences
        prefs.edit {
            putString("clone_${cloneInfo.id}_package", packageName)
            putString("clone_${cloneInfo.id}_original_name", originalAppName)
            putString("clone_${cloneInfo.id}_name", cloneName)
            putLong("clone_${cloneInfo.id}_creation_time", cloneInfo.creationTime)
            putLong("clone_${cloneInfo.id}_last_used", 0L)
            putBoolean("clone_${cloneInfo.id}_has_shortcut", false)
            putString("clone_${cloneInfo.id}_environment", cloneInfo.id)
            putString("clone_${cloneInfo.id}_badge", badgeNumber ?: "2")
            putInt("clone_${cloneInfo.id}_launch_count", 0)
            
            // Add to list of clone IDs
            val cloneIds = getCloneIds().toMutableList()
            if (!cloneIds.contains(cloneInfo.id)) {
                cloneIds.add(cloneInfo.id)
                putString(cloneIdsKey, cloneIds.joinToString(","))
            }
        }
        
        // Save custom icon if provided
        if (customIcon != null) {
            saveIconForClone(cloneInfo.id, customIcon)
        }
        
        Log.d(TAG, "Successfully saved clone $id")
        cloneInfo
    }
    
    /**
     * Update last used time for a clone
     */
    suspend fun updateLastUsed(cloneId: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating last used time for clone $cloneId")
        prefs.edit {
            putLong("clone_${cloneId}_last_used", System.currentTimeMillis())
        }
    }
    
    /**
     * Update shortcut status for a clone
     */
    suspend fun updateShortcutStatus(cloneId: String, hasShortcut: Boolean) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating shortcut status for clone $cloneId: $hasShortcut")
        prefs.edit {
            putBoolean("clone_${cloneId}_has_shortcut", hasShortcut)
        }
    }
    
    /**
     * Update launch count for a clone
     */
    suspend fun updateLaunchCount(cloneId: String, count: Int) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating launch count for clone $cloneId: $count")
        prefs.edit {
            putInt("clone_${cloneId}_launch_count", count)
        }
    }
    
    /**
     * Update environment ID for a clone
     */
    suspend fun updateEnvironmentId(cloneId: String, environmentId: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating environment ID for clone $cloneId: $environmentId")
        prefs.edit {
            putString("clone_${cloneId}_environment", environmentId)
        }
    }
    
    /**
     * Update badge number for a clone
     */
    suspend fun updateBadgeNumber(cloneId: String, badgeNumber: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating badge number for clone $cloneId: $badgeNumber")
        prefs.edit {
            putString("clone_${cloneId}_badge", badgeNumber)
        }
    }
    
    /**
     * Update clone name
     */
    suspend fun updateCloneName(cloneId: String, newName: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating name for clone $cloneId: $newName")
        prefs.edit {
            putString("clone_${cloneId}_name", newName)
        }
    }
    
    /**
     * Update clone icon
     */
    suspend fun updateCloneIcon(cloneId: String, newIcon: Bitmap) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating icon for clone $cloneId")
        saveIconForClone(cloneId, newIcon)
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Deleting clone $cloneId")
            
            // Remove from preferences
            prefs.edit {
                remove("clone_${cloneId}_package")
                remove("clone_${cloneId}_original_name")
                remove("clone_${cloneId}_name")
                remove("clone_${cloneId}_creation_time")
                remove("clone_${cloneId}_last_used")
                remove("clone_${cloneId}_has_shortcut")
                remove("clone_${cloneId}_environment")
                remove("clone_${cloneId}_badge")
                remove("clone_${cloneId}_launch_count")
                
                // Update list of clone IDs
                val cloneIds = getCloneIds().toMutableList()
                cloneIds.remove(cloneId)
                putString(cloneIdsKey, cloneIds.joinToString(","))
            }
            
            // Delete icon file
            val iconFile = File(cloneDir, "${cloneId}_icon.png")
            if (iconFile.exists()) {
                iconFile.delete()
            }
            
            Log.d(TAG, "Successfully deleted clone $cloneId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting clone $cloneId", e)
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Get total count of clones
     */
    suspend fun getCloneCount(): Int = withContext(Dispatchers.IO) {
        getCloneIds().size
    }
    
    /**
     * Check if a clone exists
     */
    suspend fun cloneExists(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        prefs.contains("clone_${cloneId}_package")
    }
    
    /**
     * Save an icon for a clone
     */
    private suspend fun saveIconForClone(cloneId: String, icon: Bitmap) = withContext(Dispatchers.IO) {
        try {
            val iconFile = File(cloneDir, "${cloneId}_icon.png")
            FileOutputStream(iconFile).use { out ->
                icon.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Log.d(TAG, "Saved icon for clone $cloneId to ${iconFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving icon for clone $cloneId", e)
            e.printStackTrace()
        }
    }
    
    /**
     * Load an icon for a clone
     */
    private suspend fun loadIconForClone(cloneId: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val iconFile = File(cloneDir, "${cloneId}_icon.png")
            if (iconFile.exists()) {
                BitmapFactory.decodeFile(iconFile.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading icon for clone $cloneId", e)
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get the list of all clone IDs
     */
    private fun getCloneIds(): List<String> {
        val idsString = prefs.getString(cloneIdsKey, "") ?: ""
        return if (idsString.isNotEmpty()) {
            idsString.split(",")
        } else {
            emptyList()
        }
    }
}