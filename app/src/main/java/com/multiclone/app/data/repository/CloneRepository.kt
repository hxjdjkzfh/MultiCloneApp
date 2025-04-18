package com.multiclone.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.utils.IconUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "clone_store")

/**
 * Repository for managing clone data
 */
@Singleton
class CloneRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository
) {
    private val clonesKey = stringPreferencesKey("clones")
    private val cloneIconsDir = File(context.filesDir, "clone_icons")
    
    init {
        // Ensure icons directory exists
        cloneIconsDir.mkdirs()
    }
    
    /**
     * Get all registered clones
     */
    fun getAllClones(): Flow<List<CloneInfo>> {
        return context.dataStore.data.map { preferences ->
            val clonesJson = preferences[clonesKey] ?: "[]"
            parseClonesList(clonesJson)
        }
    }
    
    /**
     * Get a specific clone by ID
     */
    suspend fun getCloneById(cloneId: String): CloneInfo? {
        return withContext(Dispatchers.IO) {
            val clonesJson = context.dataStore.data.map { preferences ->
                preferences[clonesKey] ?: "[]"
            }.first()
            
            parseClonesList(clonesJson).find { it.id == cloneId }
        }
    }
    
    /**
     * Create a new clone
     */
    suspend fun createClone(
        packageName: String,
        virtualEnvId: String,
        customName: String? = null,
        customIcon: Bitmap? = null
    ): String {
        return withContext(Dispatchers.IO) {
            val cloneId = UUID.randomUUID().toString()
            val appInfo = appRepository.getAppInfo(packageName)
            
            if (appInfo != null) {
                // Save custom icon if provided
                val iconPath = if (customIcon != null) {
                    val iconFile = File(cloneIconsDir, "$cloneId.png")
                    IconUtils.saveBitmapToFile(customIcon, iconFile)
                    iconFile.absolutePath
                } else {
                    ""
                }
                
                // Add to clones list
                context.dataStore.edit { preferences ->
                    val clonesJson = preferences[clonesKey] ?: "[]"
                    val clonesList = parseClonesList(clonesJson).toMutableList()
                    
                    val clone = CloneInfo(
                        id = cloneId,
                        packageName = packageName,
                        originalAppName = appInfo.appName,
                        displayName = customName ?: appInfo.appName,
                        customIcon = customIcon,
                        virtualEnvironmentId = virtualEnvId,
                        creationTimestamp = System.currentTimeMillis(),
                        lastUsedTimestamp = System.currentTimeMillis()
                    )
                    
                    clonesList.add(clone)
                    preferences[clonesKey] = serializeClonesList(clonesList)
                }
                
                cloneId
            } else {
                throw IllegalArgumentException("App with package name $packageName not found")
            }
        }
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(cloneId: String) {
        withContext(Dispatchers.IO) {
            // Delete the icon file if it exists
            val iconFile = File(cloneIconsDir, "$cloneId.png")
            if (iconFile.exists()) {
                iconFile.delete()
            }
            
            // Remove from clones list
            context.dataStore.edit { preferences ->
                val clonesJson = preferences[clonesKey] ?: "[]"
                val clonesList = parseClonesList(clonesJson).toMutableList()
                
                clonesList.removeIf { it.id == cloneId }
                preferences[clonesKey] = serializeClonesList(clonesList)
            }
        }
    }
    
    /**
     * Update the last used timestamp for a clone
     */
    suspend fun updateLastUsedTime(cloneId: String) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                val clonesJson = preferences[clonesKey] ?: "[]"
                val clonesList = parseClonesList(clonesJson).toMutableList()
                
                val updatedList = clonesList.map { clone ->
                    if (clone.id == cloneId) {
                        clone.copy(lastUsedTimestamp = System.currentTimeMillis())
                    } else {
                        clone
                    }
                }
                
                preferences[clonesKey] = serializeClonesList(updatedList)
            }
        }
    }
    
    /**
     * Parse a JSON string into a list of CloneInfo objects
     */
    private fun parseClonesList(json: String): List<CloneInfo> {
        return try {
            val clonesList = mutableListOf<CloneInfo>()
            val jsonArray = JSONArray(json)
            
            for (i in 0 until jsonArray.length()) {
                val cloneObj = jsonArray.getJSONObject(i)
                val iconPath = cloneObj.optString("iconPath", "")
                val customIcon = if (iconPath.isNotEmpty()) {
                    val iconFile = File(iconPath)
                    if (iconFile.exists()) {
                        IconUtils.loadBitmapFromFile(iconFile)
                    } else {
                        null
                    }
                } else {
                    null
                }
                
                val clone = CloneInfo(
                    id = cloneObj.getString("id"),
                    packageName = cloneObj.getString("packageName"),
                    originalAppName = cloneObj.getString("originalAppName"),
                    displayName = cloneObj.getString("displayName"),
                    customIcon = customIcon,
                    virtualEnvironmentId = cloneObj.getString("virtualEnvironmentId"),
                    creationTimestamp = cloneObj.getLong("creationTimestamp"),
                    lastUsedTimestamp = cloneObj.getLong("lastUsedTimestamp")
                )
                
                clonesList.add(clone)
            }
            
            clonesList
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Serialize a list of CloneInfo objects to a JSON string
     */
    private fun serializeClonesList(clones: List<CloneInfo>): String {
        val jsonArray = JSONArray()
        
        for (clone in clones) {
            val cloneObj = JSONObject().apply {
                put("id", clone.id)
                put("packageName", clone.packageName)
                put("originalAppName", clone.originalAppName)
                put("displayName", clone.displayName)
                put("iconPath", File(cloneIconsDir, "${clone.id}.png").absolutePath)
                put("virtualEnvironmentId", clone.virtualEnvironmentId)
                put("creationTimestamp", clone.creationTimestamp)
                put("lastUsedTimestamp", clone.lastUsedTimestamp)
            }
            
            jsonArray.put(cloneObj)
        }
        
        return jsonArray.toString()
    }
}