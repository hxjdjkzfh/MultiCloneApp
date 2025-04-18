package com.multiclone.app.domain.virtualization

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.UserManager
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages creation and lifecycle of isolated environments for app clones
 */
@Singleton
class CloneEnvironment @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "CloneEnvironment"
    private val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
    private val packageManager = context.packageManager
    private val secureRandom = SecureRandom()
    private val envPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("clone_environments", Context.MODE_PRIVATE)
    }
    
    // Base directory for all environments
    private val environmentsBaseDir: File by lazy {
        File(context.filesDir, "environments").apply { 
            if (!exists()) mkdirs() 
        }
    }
    
    /**
     * Create a new isolated environment
     * @return The ID of the created environment
     */
    suspend fun createEnvironment(cloneId: String): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "Creating environment for clone ID: $cloneId")
        
        // Generate a unique environment ID (for enhanced security)
        val environmentId = UUID.randomUUID().toString()
        
        // Create a new directory to store the environment data
        val environmentDir = File(environmentsBaseDir, environmentId)
        environmentDir.mkdirs()
        
        // Create data directories for the environment
        createEnvironmentDirectories(environmentDir)
        
        // Store mapping between clone ID and environment ID
        envPrefs.edit()
            .putString(cloneId, environmentId)
            .putLong("${environmentId}_created", System.currentTimeMillis())
            .apply()
        
        // Initialize environment with security token
        initializeEnvironmentSecurity(environmentId)
        
        // Return the environment ID
        environmentId
    }
    
    /**
     * Get environment ID from clone ID
     */
    fun getEnvironmentIdForClone(cloneId: String): String? {
        return envPrefs.getString(cloneId, null)
    }
    
    /**
     * Prepare an environment before launching an app
     */
    suspend fun prepareEnvironment(environmentId: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Preparing environment: $environmentId")
        
        val environmentDir = getEnvironmentDir(environmentId)
        
        // Ensure environment directories exist
        if (!environmentDir.exists()) {
            Log.w(TAG, "Environment directory doesn't exist, creating it: $environmentId")
            createEnvironmentDirectories(environmentDir)
            initializeEnvironmentSecurity(environmentId)
        }
        
        // Ensure runtime directories exist
        val runtimeDir = File(environmentDir, "runtime")
        if (!runtimeDir.exists()) {
            runtimeDir.mkdirs()
        }
        
        // Create a token file to validate the environment is active
        File(runtimeDir, "active").writeText(System.currentTimeMillis().toString())
        
        // Set up storage redirection
        setupStorageRedirection(environmentId)
    }
    
    /**
     * Remove an environment and clean up its resources
     */
    suspend fun removeEnvironment(environmentId: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Removing environment: $environmentId")
        
        val environmentDir = getEnvironmentDir(environmentId)
        
        // Delete environment directory and all its contents
        if (environmentDir.exists()) {
            environmentDir.deleteRecursively()
        }
        
        // Remove from preferences
        // First, find the clone ID that maps to this environment ID
        val allEntries = envPrefs.all
        val cloneIdToRemove = allEntries.entries.find { 
            it.value == environmentId 
        }?.key
        
        // Remove the mapping and metadata
        envPrefs.edit().apply {
            cloneIdToRemove?.let { remove(it) }
            remove("${environmentId}_created")
            remove("${environmentId}_security_token")
        }.apply()
    }
    
    /**
     * Get app-specific data directory in the isolated environment
     */
    fun getAppDataDir(environmentId: String, packageName: String): File {
        val dir = File(getEnvironmentDir(environmentId), "data/$packageName")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Get directory for shared files between all apps in an environment
     */
    fun getSharedDir(environmentId: String): File {
        val dir = File(getEnvironmentDir(environmentId), "shared")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Get environment base directory
     */
    private fun getEnvironmentDir(environmentId: String): File {
        return File(environmentsBaseDir, environmentId)
    }
    
    /**
     * Create standard directory structure for an environment
     */
    private fun createEnvironmentDirectories(environmentDir: File) {
        // Create standard Android data directories
        val standardDirs = listOf(
            "data",
            "shared_prefs",
            "databases",
            "cache",
            "shared",  // Shared between apps in this environment
            "runtime", // Runtime state information
            "media",   // Media files
            "config"   // Environment configuration
        )
        
        standardDirs.forEach { dirName ->
            File(environmentDir, dirName).apply {
                if (!exists()) mkdirs()
            }
        }
        
        // Create .nomedia file to prevent media scanner from indexing
        File(environmentDir, ".nomedia").createNewFile()
    }
    
    /**
     * Initialize security for the environment
     */
    private fun initializeEnvironmentSecurity(environmentId: String) {
        // Generate a secure random token for this environment
        val securityTokenBytes = ByteArray(32)
        secureRandom.nextBytes(securityTokenBytes)
        val securityToken = securityTokenBytes.joinToString("") { 
            "%02x".format(it) 
        }
        
        // Store the security token
        envPrefs.edit()
            .putString("${environmentId}_security_token", securityToken)
            .apply()
        
        // Create a security config file in the environment
        val configDir = File(getEnvironmentDir(environmentId), "config")
        configDir.mkdirs()
        
        // Store some security information in an encrypted file
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
                
            val securityFile = File(configDir, "security.dat")
            val encryptedFile = EncryptedFile.Builder(
                context,
                securityFile,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            
            encryptedFile.openFileOutput().use { outputStream ->
                val securityData = """
                    environment_id=$environmentId
                    created=${System.currentTimeMillis()}
                    device=${Build.MODEL}
                    android=${Build.VERSION.RELEASE}
                    sdk=${Build.VERSION.SDK_INT}
                """.trimIndent().toByteArray()
                
                outputStream.write(securityData)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating security file", e)
            // Fallback to unencrypted file if encryption fails
            File(configDir, "security.conf").writeText(
                "environment_id=$environmentId\ncreated=${System.currentTimeMillis()}"
            )
        }
    }
    
    /**
     * Setup storage redirection for this environment
     */
    private fun setupStorageRedirection(environmentId: String) {
        // Create virtual storage locations that will be used 
        // when intercepting file system operations
        val storageDir = File(getEnvironmentDir(environmentId), "storage")
        storageDir.mkdirs()
        
        // Create standard storage directories
        File(storageDir, "external").mkdirs() // Redirected external storage
        File(storageDir, "downloads").mkdirs() // Redirected downloads
        File(storageDir, "dcim").mkdirs() // Redirected camera
        File(storageDir, "pictures").mkdirs() // Redirected pictures
        File(storageDir, "movies").mkdirs() // Redirected videos
        File(storageDir, "music").mkdirs() // Redirected music
        
        // Create a symbolic metadata file for the storage redirection system
        val metadataFile = File(storageDir, "storage_redirect.json")
        metadataFile.writeText("""
            {
                "version": 1,
                "environmentId": "$environmentId",
                "created": ${System.currentTimeMillis()},
                "redirects": {
                    "EXTERNAL_STORAGE": "external",
                    "DOWNLOAD": "downloads",
                    "DCIM": "dcim",
                    "PICTURES": "pictures",
                    "MOVIES": "movies",
                    "MUSIC": "music"
                }
            }
        """.trimIndent())
    }
    
    /**
     * Get list of all environments
     */
    fun getAllEnvironments(): List<EnvironmentInfo> {
        val environments = mutableListOf<EnvironmentInfo>()
        
        envPrefs.all.forEach { (key, value) ->
            if (!key.contains("_") && value is String) {
                // This is a clone ID -> environment ID mapping
                val cloneId = key
                val environmentId = value
                val createdTime = envPrefs.getLong("${environmentId}_created", 0)
                
                // Check if the directory exists
                val envDir = getEnvironmentDir(environmentId)
                if (envDir.exists()) {
                    environments.add(
                        EnvironmentInfo(
                            cloneId = cloneId,
                            environmentId = environmentId,
                            createdTime = createdTime,
                            size = calculateDirSize(envDir)
                        )
                    )
                } else {
                    // Clean up orphaned preferences
                    envPrefs.edit().remove(cloneId).apply()
                }
            }
        }
        
        return environments
    }
    
    /**
     * Calculate directory size recursively
     */
    private fun calculateDirSize(dir: File): Long {
        var size: Long = 0
        
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateDirSize(file)
            } else {
                file.length()
            }
        }
        
        return size
    }
    
    /**
     * Data class representing environment information
     */
    data class EnvironmentInfo(
        val cloneId: String,
        val environmentId: String, 
        val createdTime: Long,
        val size: Long
    )
}