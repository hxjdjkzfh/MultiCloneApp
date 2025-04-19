package com.multiclone.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for managing cloned app data
 */
@Singleton
class CloneRepository @Inject constructor(
    private val context: Context
) {
    private val clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    
    // Current environment version
    private val currentEnvironmentVersion = 2
    
    // Master key for encrypted preferences
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    // Encrypted shared preferences for storing clone data
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "clones_data",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    init {
        loadClones()
    }
    
    /**
     * Get a flow of all clones
     */
    fun getClones(): Flow<List<CloneInfo>> = clones.asStateFlow()
    
    /**
     * Get a clone by ID
     */
    suspend fun getCloneById(id: String): CloneInfo? = withContext(Dispatchers.IO) {
        clones.value.find { it.id == id }
    }
    
    /**
     * Get clones by original package name
     */
    suspend fun getClonesByPackage(packageName: String): List<CloneInfo> = withContext(Dispatchers.IO) {
        clones.value.filter { it.originalPackageName == packageName }
    }
    
    /**
     * Add a new clone
     */
    suspend fun addClone(clone: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentClones = clones.value.toMutableList()
            currentClones.add(clone)
            clones.value = currentClones
            saveClones()
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to add clone")
            false
        }
    }
    
    /**
     * Update an existing clone
     */
    suspend fun updateClone(clone: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentClones = clones.value.toMutableList()
            val index = currentClones.indexOfFirst { it.id == clone.id }
            if (index != -1) {
                currentClones[index] = clone
                clones.value = currentClones
                saveClones()
                return@withContext true
            }
            false
        } catch (e: Exception) {
            Timber.e(e, "Failed to update clone")
            false
        }
    }
    
    /**
     * Delete a clone by ID
     */
    suspend fun deleteClone(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentClones = clones.value.toMutableList()
            val removed = currentClones.removeIf { it.id == id }
            if (removed) {
                clones.value = currentClones
                saveClones()
                deleteCloneDirectory(id)
                return@withContext true
            }
            false
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete clone")
            false
        }
    }
    
    /**
     * Update clone running status
     */
    suspend fun updateCloneRunningStatus(id: String, isRunning: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentClones = clones.value.toMutableList()
            val index = currentClones.indexOfFirst { it.id == id }
            if (index != -1) {
                val clone = currentClones[index]
                currentClones[index] = clone.copy(isRunning = isRunning)
                clones.value = currentClones
                saveClones()
                return@withContext true
            }
            false
        } catch (e: Exception) {
            Timber.e(e, "Failed to update clone running status")
            false
        }
    }
    
    /**
     * Update launch statistics for a clone
     */
    suspend fun updateLaunchStats(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentClones = clones.value.toMutableList()
            val index = currentClones.indexOfFirst { it.id == id }
            if (index != -1) {
                val clone = currentClones[index]
                currentClones[index] = clone.copy(
                    launchCount = clone.launchCount + 1,
                    lastLaunchTime = System.currentTimeMillis()
                )
                clones.value = currentClones
                saveClones()
                return@withContext true
            }
            false
        } catch (e: Exception) {
            Timber.e(e, "Failed to update launch stats")
            false
        }
    }
    
    /**
     * Get the base directory for all clone environments
     */
    fun getBaseClonesDirectory(): File {
        val dir = File(context.filesDir, "clone_environments")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Get the directory for a specific clone
     */
    fun getCloneDirectory(cloneId: String): File {
        val dir = File(getBaseClonesDirectory(), cloneId)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Get clones that need environment updates
     */
    suspend fun getClonesNeedingUpdate(): List<CloneInfo> = withContext(Dispatchers.IO) {
        clones.value.filter { it.needsEnvironmentUpdate(currentEnvironmentVersion) }
    }
    
    /**
     * Update a clone's environment version
     */
    suspend fun updateCloneEnvironmentVersion(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentClones = clones.value.toMutableList()
            val index = currentClones.indexOfFirst { it.id == id }
            if (index != -1) {
                val clone = currentClones[index]
                currentClones[index] = clone.copy(environmentVersion = currentEnvironmentVersion)
                clones.value = currentClones
                saveClones()
                return@withContext true
            }
            false
        } catch (e: Exception) {
            Timber.e(e, "Failed to update clone environment version")
            false
        }
    }
    
    /**
     * Load clones from storage
     */
    private fun loadClones() {
        try {
            val jsonString = sharedPreferences.getString("clones", null) ?: return
            val jsonArray = JSONArray(jsonString)
            val loadedClones = mutableListOf<CloneInfo>()
            
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                loadedClones.add(parseCloneFromJson(jsonObject))
            }
            
            clones.value = loadedClones
        } catch (e: Exception) {
            Timber.e(e, "Failed to load clones")
        }
    }
    
    /**
     * Save clones to storage
     */
    private fun saveClones() {
        try {
            val jsonArray = JSONArray()
            clones.value.forEach { clone ->
                jsonArray.put(createJsonFromClone(clone))
            }
            
            sharedPreferences.edit()
                .putString("clones", jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            Timber.e(e, "Failed to save clones")
        }
    }
    
    /**
     * Parse clone from JSON object
     */
    private fun parseCloneFromJson(jsonObject: JSONObject): CloneInfo {
        return CloneInfo(
            id = jsonObject.getString("id"),
            originalPackageName = jsonObject.getString("originalPackageName"),
            customName = jsonObject.getString("customName"),
            createdAt = jsonObject.getLong("createdAt"),
            lastLaunchTime = jsonObject.optLong("lastLaunchTime", 0),
            launchCount = jsonObject.optInt("launchCount", 0),
            customIconPath = if (jsonObject.has("customIconPath")) jsonObject.getString("customIconPath") else null,
            customColor = if (jsonObject.has("customColor")) jsonObject.getInt("customColor") else null,
            storageIsolationLevel = jsonObject.optInt("storageIsolationLevel", 1),
            useCustomNotifications = jsonObject.optBoolean("useCustomNotifications", true),
            showInLauncher = jsonObject.optBoolean("showInLauncher", true),
            environmentVersion = jsonObject.optInt("environmentVersion", 1),
            isRunning = jsonObject.optBoolean("isRunning", false)
        )
    }
    
    /**
     * Create JSON object from clone
     */
    private fun createJsonFromClone(clone: CloneInfo): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", clone.id)
        jsonObject.put("originalPackageName", clone.originalPackageName)
        jsonObject.put("customName", clone.customName)
        jsonObject.put("createdAt", clone.createdAt)
        jsonObject.put("lastLaunchTime", clone.lastLaunchTime)
        jsonObject.put("launchCount", clone.launchCount)
        if (clone.customIconPath != null) {
            jsonObject.put("customIconPath", clone.customIconPath)
        }
        if (clone.customColor != null) {
            jsonObject.put("customColor", clone.customColor)
        }
        jsonObject.put("storageIsolationLevel", clone.storageIsolationLevel)
        jsonObject.put("useCustomNotifications", clone.useCustomNotifications)
        jsonObject.put("showInLauncher", clone.showInLauncher)
        jsonObject.put("environmentVersion", clone.environmentVersion)
        jsonObject.put("isRunning", clone.isRunning)
        return jsonObject
    }
    
    /**
     * Delete a clone's directory
     */
    private fun deleteCloneDirectory(cloneId: String) {
        val dir = getCloneDirectory(cloneId)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }
}