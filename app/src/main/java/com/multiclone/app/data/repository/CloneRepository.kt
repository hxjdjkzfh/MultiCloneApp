package com.multiclone.app.data.repository

import android.content.Context
import android.util.Log
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing cloned app data
 */
@Singleton
class CloneRepository @Inject constructor(
    private val context: Context,
    private val virtualAppEngine: VirtualAppEngine
) {
    companion object {
        private const val TAG = "CloneRepository"
    }
    
    // In-memory cache of clones
    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val clones: Flow<List<CloneInfo>> = _clones.asStateFlow()
    
    // Root directory for clone data
    private val clonesRootDir by lazy {
        context.getDir("clones", Context.MODE_PRIVATE)
    }
    
    /**
     * Initialize the repository
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        Log.d(TAG, "Initializing repository")
        
        // Load existing clones
        loadExistingClones()
    }
    
    /**
     * Load all existing clones from storage
     */
    private suspend fun loadExistingClones() = withContext(Dispatchers.IO) {
        val clonesList = mutableListOf<CloneInfo>()
        
        // Check if clones directory exists
        if (!clonesRootDir.exists()) {
            clonesRootDir.mkdirs()
            _clones.value = clonesList
            return@withContext
        }
        
        // List all clone directories
        val cloneDirs = clonesRootDir.listFiles()
        if (cloneDirs == null || cloneDirs.isEmpty()) {
            _clones.value = clonesList
            return@withContext
        }
        
        // Load each clone
        for (cloneDir in cloneDirs) {
            val configFile = File(cloneDir, "config.json")
            if (!configFile.exists()) continue
            
            try {
                val json = JSONObject(configFile.readText())
                val packageName = json.optString("packageName", "")
                val displayName = json.optString("displayName", "")
                val createdAt = json.optLong("createdAt", System.currentTimeMillis())
                val lastUsed = json.optLong("lastUsed", createdAt)
                val colorHex = json.optString("colorHex", null)
                val notificationsEnabled = json.optBoolean("notificationsEnabled", true)
                val isFrozen = json.optBoolean("isFrozen", false)
                
                if (packageName.isNotEmpty()) {
                    // Try to get app icon from package manager
                    val originalIcon = try {
                        context.packageManager.getApplicationIcon(packageName)
                    } catch (e: Exception) {
                        null
                    }
                    
                    // Create clone info object
                    val cloneInfo = CloneInfo(
                        id = cloneDir.name,
                        packageName = packageName,
                        displayName = displayName,
                        originalIcon = originalIcon,
                        creationTime = createdAt,
                        lastUsedTime = lastUsed,
                        colorHex = colorHex,
                        notificationsEnabled = notificationsEnabled,
                        isFrozen = isFrozen
                    )
                    
                    clonesList.add(cloneInfo)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing clone config file", e)
            }
        }
        
        // Sort by last used time (most recent first)
        clonesList.sortByDescending { it.lastUsedTime }
        
        // Update state flow
        _clones.value = clonesList
    }
    
    /**
     * Create a new clone of an app
     */
    suspend fun createClone(
        packageName: String,
        displayName: String,
        customIcon: File? = null,
        colorHex: String? = null
    ): CloneInfo? = withContext(Dispatchers.IO) {
        try {
            // Generate a unique ID for this clone
            val cloneId = UUID.randomUUID().toString()
            
            // Create the clone using the VirtualAppEngine
            val result = virtualAppEngine.createClone(packageName, cloneId, displayName)
            
            if (result) {
                // Get original app icon
                val originalIcon = try {
                    context.packageManager.getApplicationIcon(packageName)
                } catch (e: Exception) {
                    null
                }
                
                // Create config file with additional metadata
                val cloneDir = File(clonesRootDir, cloneId)
                val configFile = File(cloneDir, "config.json")
                
                val creationTime = System.currentTimeMillis()
                
                // Create or update JSON configuration
                val json = JSONObject().apply {
                    put("packageName", packageName)
                    put("displayName", displayName)
                    put("createdAt", creationTime)
                    put("lastUsed", creationTime)
                    put("hasCustomIcon", customIcon != null)
                    put("colorHex", colorHex ?: "")
                    put("notificationsEnabled", true)
                    put("isFrozen", false)
                    put("version", 1)
                }
                
                configFile.writeText(json.toString())
                
                // If custom icon provided, save it
                if (customIcon != null && customIcon.exists()) {
                    val iconFile = File(cloneDir, "custom_icon.png")
                    customIcon.copyTo(iconFile, overwrite = true)
                }
                
                // Create clone info object
                val cloneInfo = CloneInfo(
                    id = cloneId,
                    packageName = packageName,
                    displayName = displayName,
                    originalIcon = originalIcon,
                    creationTime = creationTime,
                    lastUsedTime = creationTime,
                    colorHex = colorHex,
                    notificationsEnabled = true,
                    isFrozen = false
                )
                
                // Update clones list
                val currentList = _clones.value.toMutableList()
                currentList.add(cloneInfo)
                currentList.sortByDescending { it.lastUsedTime }
                _clones.value = currentList
                
                return@withContext cloneInfo
            }
            
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating clone", e)
            null
        }
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Delete clone files using VirtualAppEngine
            val result = virtualAppEngine.deleteClone(cloneId)
            
            if (result) {
                // Remove from in-memory list
                val currentList = _clones.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == cloneId }
                
                if (index >= 0) {
                    currentList.removeAt(index)
                    _clones.value = currentList
                }
                
                return@withContext true
            }
            
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting clone", e)
            false
        }
    }
    
    /**
     * Get a specific clone by ID
     */
    suspend fun getCloneById(cloneId: String): CloneInfo? = withContext(Dispatchers.IO) {
        _clones.value.firstOrNull { it.id == cloneId }
    }
    
    /**
     * Update the last used time for a clone
     */
    suspend fun updateLastUsedTime(cloneId: String, timestamp: Long) = withContext(Dispatchers.IO) {
        try {
            // Find the clone in memory
            val currentList = _clones.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == cloneId }
            
            if (index >= 0) {
                // Create updated clone info
                val oldClone = currentList[index]
                val updatedClone = oldClone.copy(lastUsedTime = timestamp)
                
                // Update in memory
                currentList[index] = updatedClone
                
                // Sort by last used
                currentList.sortByDescending { it.lastUsedTime }
                _clones.value = currentList
                
                // Update config file
                val configFile = File(clonesRootDir, "$cloneId/config.json")
                if (configFile.exists()) {
                    val json = JSONObject(configFile.readText())
                    json.put("lastUsed", timestamp)
                    configFile.writeText(json.toString())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating last used time", e)
        }
    }
    
    /**
     * Update the notification settings for a clone
     */
    suspend fun updateNotificationSettings(
        cloneId: String, 
        enabled: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Find the clone in memory
            val currentList = _clones.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == cloneId }
            
            if (index >= 0) {
                // Create updated clone info
                val oldClone = currentList[index]
                val updatedClone = oldClone.copy(notificationsEnabled = enabled)
                
                // Update in memory
                currentList[index] = updatedClone
                _clones.value = currentList
                
                // Update config file
                val configFile = File(clonesRootDir, "$cloneId/config.json")
                if (configFile.exists()) {
                    val json = JSONObject(configFile.readText())
                    json.put("notificationsEnabled", enabled)
                    configFile.writeText(json.toString())
                    return@withContext true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error updating notification settings", e)
            false
        }
    }
}