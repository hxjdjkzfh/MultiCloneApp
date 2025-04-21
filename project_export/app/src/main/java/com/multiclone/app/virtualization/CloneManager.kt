package com.multiclone.app.virtualization

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.multiclone.app.domain.models.ClonedApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the cloned apps in the system.
 * Handles registration, tracking, and persistence of cloned apps.
 */
@Singleton
class CloneManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val clones = mutableMapOf<String, ClonedApp>()
    private val preferences: SharedPreferences
    
    init {
        // Setup encrypted shared preferences for storing clone data
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
            
        preferences = EncryptedSharedPreferences.create(
            context,
            CLONE_PREFERENCES_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        // Load existing clones
        loadClones()
    }
    
    /**
     * Get all cloned apps
     */
    suspend fun getAllClones(): List<ClonedApp> = withContext(Dispatchers.IO) {
        return@withContext clones.values.toList()
    }
    
    /**
     * Get a specific cloned app by ID
     */
    fun getClone(cloneId: String): ClonedApp? {
        return clones[cloneId]
    }
    
    /**
     * Get all clones for a specific package
     */
    fun getClonesByPackage(packageName: String): List<ClonedApp> {
        return clones.values.filter { it.originalPackageName == packageName }
    }
    
    /**
     * Get the number of clones for a package
     */
    fun getCloneCount(packageName: String): Int {
        return getClonesByPackage(packageName).size
    }
    
    /**
     * Register a new clone
     */
    fun registerClone(clonedApp: ClonedApp) {
        clones[clonedApp.cloneId] = clonedApp
        saveClones()
    }
    
    /**
     * Unregister a clone
     */
    fun unregisterClone(cloneId: String) {
        clones.remove(cloneId)
        saveClones()
    }
    
    /**
     * Update the last used time for a clone
     */
    fun updateLastUsed(cloneId: String) {
        clones[cloneId]?.let { clone ->
            clones[cloneId] = clone.copy(
                lastUsed = System.currentTimeMillis(),
                isRunning = true
            )
            saveClones()
        }
    }
    
    /**
     * Update a clone's running status
     */
    fun updateRunningStatus(cloneId: String, isRunning: Boolean) {
        clones[cloneId]?.let { clone ->
            clones[cloneId] = clone.copy(isRunning = isRunning)
            saveClones()
        }
    }
    
    /**
     * Save all clones to persistent storage
     */
    private fun saveClones() {
        try {
            val cloneMap = mutableMapOf<String, String>()
            
            // Serialize each clone
            clones.forEach { (cloneId, clonedApp) ->
                val serializedClone = json.encodeToString(clonedApp)
                cloneMap[cloneId] = serializedClone
            }
            
            // Save to preferences
            with(preferences.edit()) {
                // Clear existing data
                clear()
                
                // Store each clone
                cloneMap.forEach { (cloneId, serializedClone) ->
                    putString(cloneId, serializedClone)
                }
                
                // Apply changes
                apply()
            }
            
            Timber.d("Saved ${clones.size} clones")
        } catch (e: Exception) {
            Timber.e(e, "Error saving clones")
        }
    }
    
    /**
     * Load clones from persistent storage
     */
    private fun loadClones() {
        try {
            clones.clear()
            
            // Get all stored clone IDs
            val allClones = preferences.all
            
            // Deserialize each clone
            allClones.forEach { (cloneId, serializedClone) ->
                if (serializedClone is String) {
                    try {
                        val clonedApp = json.decodeFromString<ClonedApp>(serializedClone)
                        clones[cloneId] = clonedApp
                    } catch (e: Exception) {
                        Timber.e(e, "Error deserializing clone $cloneId")
                    }
                }
            }
            
            Timber.d("Loaded ${clones.size} clones")
        } catch (e: Exception) {
            Timber.e(e, "Error loading clones")
        }
    }
    
    companion object {
        private const val CLONE_PREFERENCES_FILE = "multiclone_app_clones"
    }
}