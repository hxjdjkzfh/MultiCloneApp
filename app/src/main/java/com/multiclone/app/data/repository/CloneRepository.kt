package com.multiclone.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File

/**
 * Repository interface for managing cloned apps.
 */
interface CloneRepository {
    /**
     * Get a list of all cloned apps.
     * 
     * @return List of CloneInfo objects
     */
    suspend fun getAllClones(): List<CloneInfo>
    
    /**
     * Get information about a specific clone by ID.
     * 
     * @param cloneId The unique ID of the clone
     * @return CloneInfo for the specified ID, or null if not found
     */
    suspend fun getClone(cloneId: String): CloneInfo?
    
    /**
     * Save a clone.
     * 
     * @param cloneInfo The clone information to save
     * @return Whether the operation was successful
     */
    suspend fun saveClone(cloneInfo: CloneInfo): Boolean
    
    /**
     * Update an existing clone.
     * 
     * @param cloneInfo The updated clone information
     * @return Whether the operation was successful
     */
    suspend fun updateClone(cloneInfo: CloneInfo): Boolean
    
    /**
     * Delete a clone.
     * 
     * @param cloneId The unique ID of the clone to delete
     * @return Whether the operation was successful
     */
    suspend fun deleteClone(cloneId: String): Boolean
}

/**
 * Implementation of CloneRepository that uses EncryptedSharedPreferences to store clone information.
 * 
 * @param context Android context
 */
class CloneRepositoryImpl(private val context: Context) : CloneRepository {
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "multiclone_clones",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    /**
     * Get a list of all cloned apps.
     * 
     * @return List of CloneInfo objects
     */
    override suspend fun getAllClones(): List<CloneInfo> = withContext(Dispatchers.IO) {
        val clones = mutableListOf<CloneInfo>()
        
        try {
            // Get all keys from shared preferences
            val allKeys = sharedPreferences.all.keys
            
            // Get each clone by key
            for (key in allKeys) {
                if (key.startsWith("clone_")) {
                    val jsonString = sharedPreferences.getString(key, null)
                    jsonString?.let {
                        try {
                            val clone = json.decodeFromString<CloneInfo>(it)
                            clones.add(clone)
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing clone JSON: $it")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading all clones")
        }
        
        return@withContext clones
    }
    
    /**
     * Get information about a specific clone by ID.
     * 
     * @param cloneId The unique ID of the clone
     * @return CloneInfo for the specified ID, or null if not found
     */
    override suspend fun getClone(cloneId: String): CloneInfo? = withContext(Dispatchers.IO) {
        try {
            val key = "clone_$cloneId"
            val jsonString = sharedPreferences.getString(key, null)
            
            jsonString?.let {
                return@withContext json.decodeFromString<CloneInfo>(it)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting clone: $cloneId")
        }
        
        return@withContext null
    }
    
    /**
     * Save a clone.
     * 
     * @param cloneInfo The clone information to save
     * @return Whether the operation was successful
     */
    override suspend fun saveClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val key = "clone_${cloneInfo.id}"
            val jsonString = json.encodeToString(cloneInfo)
            
            sharedPreferences.edit()
                .putString(key, jsonString)
                .apply()
            
            // Create clone directory if it doesn't exist
            createCloneDirectory(cloneInfo.id)
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error saving clone: ${cloneInfo.id}")
            return@withContext false
        }
    }
    
    /**
     * Update an existing clone.
     * 
     * @param cloneInfo The updated clone information
     * @return Whether the operation was successful
     */
    override suspend fun updateClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val key = "clone_${cloneInfo.id}"
            
            // Check if the clone exists
            if (!sharedPreferences.contains(key)) {
                Timber.w("Attempted to update non-existent clone: ${cloneInfo.id}")
                return@withContext false
            }
            
            val jsonString = json.encodeToString(cloneInfo)
            
            sharedPreferences.edit()
                .putString(key, jsonString)
                .apply()
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error updating clone: ${cloneInfo.id}")
            return@withContext false
        }
    }
    
    /**
     * Delete a clone.
     * 
     * @param cloneId The unique ID of the clone to delete
     * @return Whether the operation was successful
     */
    override suspend fun deleteClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val key = "clone_$cloneId"
            
            // Remove from shared preferences
            sharedPreferences.edit()
                .remove(key)
                .apply()
            
            // Delete clone directory
            deleteCloneDirectory(cloneId)
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error deleting clone: $cloneId")
            return@withContext false
        }
    }
    
    /**
     * Create a directory for storing clone data.
     * 
     * @param cloneId The unique ID of the clone
     */
    private fun createCloneDirectory(cloneId: String) {
        try {
            val cloneDir = File(context.filesDir, "clones/$cloneId")
            if (!cloneDir.exists()) {
                cloneDir.mkdirs()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating clone directory for clone: $cloneId")
        }
    }
    
    /**
     * Delete a clone's data directory.
     * 
     * @param cloneId The unique ID of the clone
     */
    private fun deleteCloneDirectory(cloneId: String) {
        try {
            val cloneDir = File(context.filesDir, "clones/$cloneId")
            if (cloneDir.exists()) {
                cloneDir.deleteRecursively()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting clone directory for clone: $cloneId")
        }
    }
}