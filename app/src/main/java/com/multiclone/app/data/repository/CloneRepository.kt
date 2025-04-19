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
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing cloned app data
 */
@Singleton
class CloneRepository @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val PREFS_FILENAME = "multiclone_data"
        private const val KEY_CLONES = "clones"
    }
    
    // StateFlow for all clones
    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val clones: Flow<List<CloneInfo>> = _clones.asStateFlow()
    
    // Flag to track initialization
    private var isInitialized = false
    
    /**
     * Initialize the repository by loading clones from storage
     */
    suspend fun initialize() {
        if (isInitialized) return
        
        withContext(Dispatchers.IO) {
            loadClones()
            isInitialized = true
        }
    }
    
    /**
     * Add a new clone
     */
    suspend fun addClone(clone: CloneInfo) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        currentClones.add(clone)
        _clones.value = currentClones
        
        saveClones()
    }
    
    /**
     * Update an existing clone
     */
    suspend fun updateClone(clone: CloneInfo) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        val index = currentClones.indexOfFirst { it.id == clone.id }
        
        if (index != -1) {
            currentClones[index] = clone
            _clones.value = currentClones
            
            saveClones()
        }
    }
    
    /**
     * Delete a clone by ID
     */
    suspend fun deleteClone(cloneId: String) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        val removed = currentClones.removeIf { it.id == cloneId }
        
        if (removed) {
            _clones.value = currentClones
            saveClones()
        }
    }
    
    /**
     * Update notification settings for a clone
     */
    suspend fun updateNotificationSettings(cloneId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        val index = currentClones.indexOfFirst { it.id == cloneId }
        
        if (index != -1) {
            val clone = currentClones[index]
            currentClones[index] = clone.copy(notificationsEnabled = enabled)
            _clones.value = currentClones
            
            saveClones()
        }
    }
    
    /**
     * Update last used time for a clone
     */
    suspend fun updateLastUsedTime(cloneId: String) = withContext(Dispatchers.IO) {
        val currentClones = _clones.value.toMutableList()
        val index = currentClones.indexOfFirst { it.id == cloneId }
        
        if (index != -1) {
            val clone = currentClones[index]
            currentClones[index] = clone.copy(lastUsedDate = Date())
            _clones.value = currentClones
            
            saveClones()
        }
    }
    
    /**
     * Get an encrypted shared preferences instance
     */
    private fun getEncryptedSharedPreferences(): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        return EncryptedSharedPreferences.create(
            context,
            PREFS_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }
    
    /**
     * Load clones from encrypted shared preferences
     */
    private fun loadClones() {
        try {
            val prefs = getEncryptedSharedPreferences()
            val clonesString = prefs.getString(KEY_CLONES, null)
            
            if (clonesString != null) {
                val clonesArray = JSONArray(clonesString)
                val clonesList = mutableListOf<CloneInfo>()
                
                for (i in 0 until clonesArray.length()) {
                    val cloneJson = clonesArray.getJSONObject(i)
                    val cloneMap = jsonObjectToMap(cloneJson)
                    val clone = CloneInfo.fromMap(cloneMap)
                    clonesList.add(clone)
                }
                
                _clones.value = clonesList
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Save clones to encrypted shared preferences
     */
    private fun saveClones() {
        try {
            val prefs = getEncryptedSharedPreferences()
            val clonesArray = JSONArray()
            
            for (clone in _clones.value) {
                val cloneMap = CloneInfo.toMap(clone)
                val cloneJson = JSONObject(cloneMap)
                clonesArray.put(cloneJson)
            }
            
            prefs.edit().putString(KEY_CLONES, clonesArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Convert JSON object to a map
     */
    private fun jsonObjectToMap(jsonObject: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = jsonObject.keys()
        
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.get(key)
            
            map[key] = value
        }
        
        return map
    }
}