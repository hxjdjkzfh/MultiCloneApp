package com.multiclone.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to handle operations related to cloned applications
 */
@Singleton
class CloneRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
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
        emit(clones)
    }.flowOn(Dispatchers.IO)
    
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
            environmentId = environmentId
        )
    }
    
    /**
     * Save a new clone
     */
    suspend fun saveClone(
        packageName: String,
        originalAppName: String,
        cloneName: String,
        customIcon: Bitmap? = null
    ): CloneInfo = withContext(Dispatchers.IO) {
        val cloneInfo = CloneInfo(
            packageName = packageName,
            originalAppName = originalAppName,
            cloneName = cloneName,
            customIcon = customIcon,
            creationTime = System.currentTimeMillis()
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
        
        cloneInfo
    }
    
    /**
     * Update last used time for a clone
     */
    suspend fun updateLastUsed(cloneId: String) = withContext(Dispatchers.IO) {
        prefs.edit {
            putLong("clone_${cloneId}_last_used", System.currentTimeMillis())
        }
    }
    
    /**
     * Update shortcut status for a clone
     */
    suspend fun updateShortcutStatus(cloneId: String, hasShortcut: Boolean) = withContext(Dispatchers.IO) {
        prefs.edit {
            putBoolean("clone_${cloneId}_has_shortcut", hasShortcut)
        }
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Remove from preferences
            prefs.edit {
                remove("clone_${cloneId}_package")
                remove("clone_${cloneId}_original_name")
                remove("clone_${cloneId}_name")
                remove("clone_${cloneId}_creation_time")
                remove("clone_${cloneId}_last_used")
                remove("clone_${cloneId}_has_shortcut")
                remove("clone_${cloneId}_environment")
                
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
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
        } catch (e: Exception) {
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