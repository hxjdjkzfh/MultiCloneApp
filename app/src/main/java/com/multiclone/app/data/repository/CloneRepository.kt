package com.multiclone.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing clone data persistence
 */
@Singleton
class CloneRepository @Inject constructor(
    private val context: Context
) {
    private val TAG = "CloneRepository"
    private val PREFS_NAME = "multiclone_preferences"
    private val KEY_CLONES = "clones_data"
    
    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    private val iconCacheDir: File by lazy {
        File(context.cacheDir, "clone_icons").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * Save a clone to persistent storage
     */
    suspend fun saveClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            // Save the icon if present
            cloneInfo.icon?.let { icon ->
                saveIconToFile(cloneInfo.id, icon)
            }
            
            // Get current clones and add the new one
            val clones = getAllClones().toMutableList()
            val existingIndex = clones.indexOfFirst { it.id == cloneInfo.id }
            
            if (existingIndex >= 0) {
                // Update existing clone
                clones[existingIndex] = cloneInfo
            } else {
                // Add new clone
                clones.add(cloneInfo)
            }
            
            // Save to preferences
            val jsonArray = JSONArray()
            clones.forEach { clone ->
                jsonArray.put(cloneToJson(clone))
            }
            
            preferences.edit()
                .putString(KEY_CLONES, jsonArray.toString())
                .apply()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving clone", e)
            false
        }
    }
    
    /**
     * Get all saved clones
     */
    suspend fun getAllClones(): List<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            val clonesJson = preferences.getString(KEY_CLONES, null) ?: return@withContext emptyList()
            val jsonArray = JSONArray(clonesJson)
            val clones = mutableListOf<CloneInfo>()
            
            for (i in 0 until jsonArray.length()) {
                val cloneJson = jsonArray.getJSONObject(i)
                val clone = jsonToClone(cloneJson)
                clone?.let { clones.add(it) }
            }
            
            // Sort by last used, most recent first
            clones.sortedByDescending { it.lastUsedAt }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all clones", e)
            emptyList()
        }
    }
    
    /**
     * Get a specific clone by ID
     */
    suspend fun getCloneById(id: String): CloneInfo? = withContext(Dispatchers.IO) {
        try {
            getAllClones().find { it.id == id }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting clone by ID", e)
            null
        }
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val clones = getAllClones().toMutableList()
            val removed = clones.removeIf { it.id == id }
            
            if (removed) {
                // Delete the icon file
                val iconFile = File(iconCacheDir, "$id.png")
                if (iconFile.exists()) {
                    iconFile.delete()
                }
                
                // Update preferences
                val jsonArray = JSONArray()
                clones.forEach { clone ->
                    jsonArray.put(cloneToJson(clone))
                }
                
                preferences.edit()
                    .putString(KEY_CLONES, jsonArray.toString())
                    .apply()
            }
            
            removed
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting clone", e)
            false
        }
    }
    
    /**
     * Update the last used time for a clone
     */
    suspend fun updateLastUsedTime(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val clone = getCloneById(id) ?: return@withContext false
            val updatedClone = clone.copy(lastUsedAt = System.currentTimeMillis())
            saveClone(updatedClone)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating last used time", e)
            false
        }
    }
    
    /**
     * Get the next available clone index for a package
     */
    suspend fun getNextCloneIndex(packageName: String): Int = withContext(Dispatchers.IO) {
        val clones = getAllClones().filter { it.packageName == packageName }
        if (clones.isEmpty()) return@withContext 1
        
        val maxIndex = clones.maxOfOrNull { it.cloneIndex } ?: 0
        maxIndex + 1
    }
    
    /**
     * Convert a CloneInfo object to JSON
     */
    private fun cloneToJson(cloneInfo: CloneInfo): JSONObject {
        return JSONObject().apply {
            put("id", cloneInfo.id)
            put("packageName", cloneInfo.packageName)
            put("originalAppName", cloneInfo.originalAppName)
            put("customName", cloneInfo.customName ?: JSONObject.NULL)
            put("cloneIndex", cloneInfo.cloneIndex)
            put("createdAt", cloneInfo.createdAt)
            put("lastUsedAt", cloneInfo.lastUsedAt)
            // We don't store the bitmap in JSON, just a flag if it exists
            put("hasIcon", cloneInfo.icon != null)
        }
    }
    
    /**
     * Convert JSON to a CloneInfo object
     */
    private fun jsonToClone(json: JSONObject): CloneInfo? {
        return try {
            val id = json.getString("id")
            val hasIcon = json.optBoolean("hasIcon", false)
            val iconBitmap = if (hasIcon) loadIconFromFile(id) else null
            
            CloneInfo(
                id = id,
                packageName = json.getString("packageName"),
                originalAppName = json.getString("originalAppName"),
                customName = if (json.has("customName") && !json.isNull("customName")) 
                    json.getString("customName") else null,
                icon = iconBitmap,
                cloneIndex = json.getInt("cloneIndex"),
                createdAt = json.getLong("createdAt"),
                lastUsedAt = json.getLong("lastUsedAt")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing clone from JSON", e)
            null
        }
    }
    
    /**
     * Save an icon bitmap to a file
     */
    private fun saveIconToFile(id: String, icon: Bitmap) {
        try {
            val iconFile = File(iconCacheDir, "$id.png")
            FileOutputStream(iconFile).use { out ->
                icon.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving icon to file", e)
        }
    }
    
    /**
     * Load an icon bitmap from a file
     */
    private fun loadIconFromFile(id: String): Bitmap? {
        try {
            val iconFile = File(iconCacheDir, "$id.png")
            if (iconFile.exists()) {
                return BitmapFactory.decodeFile(iconFile.absolutePath)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading icon from file", e)
        }
        return null
    }
}