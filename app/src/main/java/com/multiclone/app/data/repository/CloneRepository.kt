package com.multiclone.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.multiclone.app.data.model.CloneInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing cloned applications data
 */
@Singleton
class CloneRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val clones: Flow<List<CloneInfo>> = _clones.asStateFlow()
    
    // Using EncryptedSharedPreferences for secure storage
    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            "clone_data",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    /**
     * Initialize the repository and load saved clones
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        val savedClones = loadClonesFromStorage()
        _clones.value = savedClones
    }
    
    /**
     * Create a new clone
     */
    suspend fun createClone(packageName: String, displayName: String): CloneInfo? = withContext(Dispatchers.IO) {
        // Create a storage location for the clone
        val storageDir = context.getDir("clones", Context.MODE_PRIVATE).absolutePath + "/${packageName}_${System.currentTimeMillis()}"
        
        try {
            // Create the clone info
            val cloneInfo = CloneInfo.create(packageName, displayName, storageDir)
            
            // Save to shared preferences
            saveClone(cloneInfo)
            
            // Update the mutable state flow
            val currentClones = _clones.value.toMutableList()
            currentClones.add(cloneInfo)
            _clones.value = currentClones
            
            return@withContext cloneInfo
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
    
    /**
     * Delete a clone by ID
     */
    suspend fun deleteClone(cloneId: String) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        val cloneToRemove = currentClones.find { it.id == cloneId } ?: return@withContext
        
        // Remove from shared preferences
        encryptedPrefs.edit().remove(cloneId).apply()
        
        // Remove from memory
        currentClones.remove(cloneToRemove)
        _clones.value = currentClones
    }
    
    /**
     * Update notification settings for a clone
     */
    suspend fun updateNotificationSettings(cloneId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        val cloneIndex = currentClones.indexOfFirst { it.id == cloneId }
        
        if (cloneIndex != -1) {
            val clone = currentClones[cloneIndex]
            val updatedClone = clone.copy(notificationsEnabled = enabled)
            
            // Save updated clone
            saveClone(updatedClone)
            
            // Update in memory
            currentClones[cloneIndex] = updatedClone
            _clones.value = currentClones
        }
    }
    
    /**
     * Update last used time for a clone
     */
    suspend fun updateLastUsedTime(cloneId: String) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        val cloneIndex = currentClones.indexOfFirst { it.id == cloneId }
        
        if (cloneIndex != -1) {
            val clone = currentClones[cloneIndex]
            val updatedClone = clone.copy(lastUsedTime = System.currentTimeMillis())
            
            // Save updated clone
            saveClone(updatedClone)
            
            // Update in memory
            currentClones[cloneIndex] = updatedClone
            _clones.value = currentClones
        }
    }
    
    /**
     * Load all clones from encrypted storage
     */
    private fun loadClonesFromStorage(): List<CloneInfo> {
        val clones = mutableListOf<CloneInfo>()
        
        try {
            // Read all preferences
            val allEntries = encryptedPrefs.all
            
            for ((_, value) in allEntries) {
                if (value is String) {
                    try {
                        val clone = deserializeClone(value)
                        clones.add(clone)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Skip invalid entries
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return clones
    }
    
    /**
     * Save a clone to encrypted storage
     */
    private fun saveClone(clone: CloneInfo) {
        val serialized = serializeClone(clone)
        encryptedPrefs.edit().putString(clone.id, serialized).apply()
    }
    
    /**
     * Serialize a CloneInfo object to JSON string
     */
    private fun serializeClone(clone: CloneInfo): String {
        val json = JSONObject()
        json.put("id", clone.id)
        json.put("packageName", clone.packageName)
        json.put("displayName", clone.displayName)
        json.put("iconPath", clone.iconPath ?: "")
        json.put("creationTime", clone.creationTime)
        json.put("lastUsedTime", clone.lastUsedTime)
        json.put("notificationsEnabled", clone.notificationsEnabled)
        json.put("storageLocation", clone.storageLocation)
        
        // Serialize custom settings
        val settingsJson = JSONObject()
        for ((key, value) in clone.customSettings) {
            settingsJson.put(key, value)
        }
        json.put("customSettings", settingsJson)
        
        return json.toString()
    }
    
    /**
     * Deserialize a JSON string to CloneInfo object
     */
    private fun deserializeClone(serialized: String): CloneInfo {
        val json = JSONObject(serialized)
        
        // Parse custom settings
        val settingsJson = json.optJSONObject("customSettings") ?: JSONObject()
        val settings = mutableMapOf<String, String>()
        
        settingsJson.keys().forEach { key ->
            settings[key] = settingsJson.optString(key, "")
        }
        
        return CloneInfo(
            id = json.getString("id"),
            packageName = json.getString("packageName"),
            displayName = json.getString("displayName"),
            iconPath = json.optString("iconPath", null).let { if (it.isEmpty()) null else it },
            creationTime = json.getLong("creationTime"),
            lastUsedTime = json.getLong("lastUsedTime"),
            notificationsEnabled = json.getBoolean("notificationsEnabled"),
            storageLocation = json.getString("storageLocation"),
            customSettings = settings
        )
    }
}