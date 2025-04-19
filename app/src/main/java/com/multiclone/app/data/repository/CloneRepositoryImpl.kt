package com.multiclone.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CloneRepository responsible for managing persistent storage of cloned app configurations.
 */
@Singleton
class CloneRepositoryImpl @Inject constructor(
    private val context: Context
) : CloneRepository {
    companion object {
        private const val CLONES_DIRECTORY = "clones"
        private const val CLONES_LIST_FILE = "clones_list.json"
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    
    /**
     * Observable flow of cloned apps
     */
    override val clones: Flow<List<CloneInfo>> = _clones.asStateFlow()
    
    /**
     * Adds a new cloned app to the repository
     * 
     * @param clone The clone information to save
     * @return Success status of the operation
     */
    override suspend fun addClone(clone: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Adding clone ${clone.id} (${clone.packageName})")
            
            // Get current list of clones
            val currentClones = _clones.value.toMutableList()
            
            // Check if a clone with this ID already exists
            if (currentClones.any { it.id == clone.id }) {
                Timber.e("Clone with ID ${clone.id} already exists")
                return@withContext false
            }
            
            // Add the new clone
            currentClones.add(clone)
            
            // Save to storage
            if (!saveClonesToStorage(currentClones)) {
                Timber.e("Failed to save clones to storage")
                return@withContext false
            }
            
            // Update the flow
            _clones.value = currentClones
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error adding clone ${clone.id}")
            return@withContext false
        }
    }
    
    /**
     * Updates an existing cloned app in the repository
     * 
     * @param clone The updated clone information
     * @return Success status of the operation
     */
    override suspend fun updateClone(clone: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Updating clone ${clone.id}")
            
            // Get current list of clones
            val currentClones = _clones.value.toMutableList()
            
            // Find and replace the clone
            val index = currentClones.indexOfFirst { it.id == clone.id }
            if (index == -1) {
                Timber.e("Clone with ID ${clone.id} not found")
                return@withContext false
            }
            
            currentClones[index] = clone
            
            // Save to storage
            if (!saveClonesToStorage(currentClones)) {
                Timber.e("Failed to save clones to storage")
                return@withContext false
            }
            
            // Update the flow
            _clones.value = currentClones
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error updating clone ${clone.id}")
            return@withContext false
        }
    }
    
    /**
     * Removes a cloned app from the repository
     * 
     * @param cloneId The ID of the clone to remove
     * @return Success status of the operation
     */
    override suspend fun removeClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Removing clone $cloneId")
            
            // Get current list of clones
            val currentClones = _clones.value.toMutableList()
            
            // Remove the clone
            val removed = currentClones.removeIf { it.id == cloneId }
            if (!removed) {
                Timber.e("Clone with ID $cloneId not found")
                return@withContext false
            }
            
            // Save to storage
            if (!saveClonesToStorage(currentClones)) {
                Timber.e("Failed to save clones to storage")
                return@withContext false
            }
            
            // Update the flow
            _clones.value = currentClones
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error removing clone $cloneId")
            return@withContext false
        }
    }
    
    /**
     * Gets a specific clone by its ID
     * 
     * @param cloneId The ID of the clone to retrieve
     * @return The clone info or null if not found
     */
    override suspend fun getCloneById(cloneId: String): CloneInfo? = withContext(Dispatchers.IO) {
        try {
            return@withContext _clones.value.find { it.id == cloneId }
        } catch (e: Exception) {
            Timber.e(e, "Error getting clone $cloneId")
            return@withContext null
        }
    }
    
    /**
     * Loads all clones from storage
     * 
     * @return Success status of the operation
     */
    override suspend fun loadClones(): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Loading clones from storage")
            
            val clonesFile = getClonesListFile()
            if (!clonesFile.exists()) {
                Timber.d("Clones file doesn't exist yet, using empty list")
                _clones.value = emptyList()
                return@withContext true
            }
            
            // Read from the encrypted file
            val masterKey = getMasterKey()
            val encryptedFile = EncryptedFile.Builder(
                context,
                clonesFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            
            val byteArrayOutputStream = ByteArrayOutputStream()
            encryptedFile.openFileInput().use { inputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead)
                }
            }
            
            val jsonString = byteArrayOutputStream.toString("UTF-8")
            val loadedClones: List<CloneInfo> = json.decodeFromString(jsonString)
            
            Timber.d("Loaded ${loadedClones.size} clones from storage")
            _clones.value = loadedClones
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error loading clones")
            // If there was an error, set an empty list to avoid null issues
            _clones.value = emptyList()
            return@withContext false
        }
    }
    
    /**
     * Saves clones to encrypted storage
     */
    private suspend fun saveClonesToStorage(clones: List<CloneInfo>): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Saving ${clones.size} clones to storage")
            
            // Create the directory if it doesn't exist
            val clonesDir = getClonesDirectory()
            if (!clonesDir.exists() && !clonesDir.mkdirs()) {
                Timber.e("Failed to create clones directory")
                return@withContext false
            }
            
            // Serialize the clones list to JSON
            val jsonString = json.encodeToString(clones)
            
            // Create an encrypted file
            val masterKey = getMasterKey()
            val clonesFile = getClonesListFile()
            val encryptedFile = EncryptedFile.Builder(
                context,
                clonesFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            
            // Write to the encrypted file
            encryptedFile.openFileOutput().use { outputStream ->
                outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            
            Timber.d("Clones saved successfully")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error saving clones")
            return@withContext false
        }
    }
    
    /**
     * Gets the master key for encryption
     */
    private fun getMasterKey(): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    
    /**
     * Gets the directory for storing clone information
     */
    private fun getClonesDirectory(): File {
        return File(context.filesDir, CLONES_DIRECTORY)
    }
    
    /**
     * Gets the file for storing the list of clones
     */
    private fun getClonesListFile(): File {
        return File(getClonesDirectory(), CLONES_LIST_FILE)
    }
}