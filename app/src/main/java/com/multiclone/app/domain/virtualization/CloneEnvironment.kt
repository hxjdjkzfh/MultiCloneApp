package com.multiclone.app.domain.virtualization

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the isolated environments for cloned applications
 * Handles environment creation, destruction, and isolation
 */
@Singleton
class CloneEnvironment @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "CloneEnvironment"
        private const val ENV_ROOT_DIR = "virtual_environments"
        private const val CLONE_ENVIRONMENTS_PREFS = "clone_environments_prefs"
    }

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val environmentsPrefs by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                CLONE_ENVIRONMENTS_PREFS,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating encrypted preferences", e)
            // Fallback to regular preferences
            context.getSharedPreferences(CLONE_ENVIRONMENTS_PREFS, Context.MODE_PRIVATE)
        }
    }

    /**
     * Creates a new isolated environment for a cloned app
     *
     * @param environmentId The ID of the environment to create
     * @return The path to the new environment, or null if creation failed
     */
    fun createEnvironment(environmentId: String): String? {
        try {
            Log.d(TAG, "Creating environment: $environmentId")
            
            // Create root directory if it doesn't exist
            val rootDir = File(context.filesDir, ENV_ROOT_DIR)
            if (!rootDir.exists() && !rootDir.mkdirs()) {
                Log.e(TAG, "Failed to create root directory")
                return null
            }
            
            // Create environment directory
            val envDir = File(rootDir, environmentId)
            if (!envDir.exists() && !envDir.mkdirs()) {
                Log.e(TAG, "Failed to create environment directory")
                return null
            }
            
            // Create subdirectories for app data
            val dataDirs = arrayOf(
                File(envDir, "data"),
                File(envDir, "cache"),
                File(envDir, "files"),
                File(envDir, "databases"),
                File(envDir, "shared_prefs")
            )
            
            for (dir in dataDirs) {
                if (!dir.exists() && !dir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory: ${dir.name}")
                    return null
                }
            }
            
            Log.d(TAG, "Environment created successfully: $environmentId")
            
            return envDir.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error creating environment", e)
            return null
        }
    }

    /**
     * Destroys an environment
     *
     * @param environmentId The ID of the environment to destroy
     * @return True if the environment was destroyed successfully, false otherwise
     */
    fun destroyEnvironment(environmentId: String): Boolean {
        try {
            Log.d(TAG, "Destroying environment: $environmentId")
            
            val rootDir = File(context.filesDir, ENV_ROOT_DIR)
            val envDir = File(rootDir, environmentId)
            
            if (!envDir.exists()) {
                Log.d(TAG, "Environment directory doesn't exist")
                return true
            }
            
            // Delete environment directory recursively
            val success = envDir.deleteRecursively()
            
            if (success) {
                Log.d(TAG, "Environment destroyed successfully: $environmentId")
            } else {
                Log.e(TAG, "Failed to destroy environment: $environmentId")
            }
            
            return success
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying environment", e)
            return false
        }
    }

    /**
     * Gets the path to an environment
     *
     * @param environmentId The ID of the environment
     * @return The path to the environment, or null if it doesn't exist
     */
    fun getEnvironmentPath(environmentId: String): String? {
        try {
            val rootDir = File(context.filesDir, ENV_ROOT_DIR)
            val envDir = File(rootDir, environmentId)
            
            if (!envDir.exists()) {
                Log.d(TAG, "Environment directory doesn't exist: $environmentId")
                return null
            }
            
            return envDir.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error getting environment path", e)
            return null
        }
    }

    /**
     * Registers an environment for a clone
     *
     * @param cloneId The ID of the clone
     * @param environmentId The ID of the environment
     */
    fun registerEnvironmentForClone(cloneId: String, environmentId: String) {
        try {
            Log.d(TAG, "Registering environment $environmentId for clone $cloneId")
            
            environmentsPrefs.edit()
                .putString(cloneId, environmentId)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error registering environment for clone", e)
        }
    }

    /**
     * Unregisters an environment for a clone
     *
     * @param cloneId The ID of the clone
     */
    fun unregisterEnvironmentForClone(cloneId: String) {
        try {
            Log.d(TAG, "Unregistering environment for clone $cloneId")
            
            environmentsPrefs.edit()
                .remove(cloneId)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering environment for clone", e)
        }
    }

    /**
     * Gets the environment ID for a clone
     *
     * @param cloneId The ID of the clone
     * @return The ID of the environment, or null if not found
     */
    fun getEnvironmentIdForClone(cloneId: String): String? {
        try {
            val environmentId = environmentsPrefs.getString(cloneId, null)
            Log.d(TAG, "Environment ID for clone $cloneId: $environmentId")
            return environmentId
        } catch (e: Exception) {
            Log.e(TAG, "Error getting environment ID for clone", e)
            return null
        }
    }

    /**
     * Gets the data directory for an environment
     *
     * @param environmentId The ID of the environment
     * @return The data directory, or null if not found
     */
    fun getEnvironmentDataDir(environmentId: String): File? {
        try {
            val envPath = getEnvironmentPath(environmentId) ?: return null
            val dataDir = File(envPath, "data")
            
            if (!dataDir.exists()) {
                dataDir.mkdirs()
            }
            
            return dataDir
        } catch (e: Exception) {
            Log.e(TAG, "Error getting environment data directory", e)
            return null
        }
    }

    /**
     * Gets the cache directory for an environment
     *
     * @param environmentId The ID of the environment
     * @return The cache directory, or null if not found
     */
    fun getEnvironmentCacheDir(environmentId: String): File? {
        try {
            val envPath = getEnvironmentPath(environmentId) ?: return null
            val cacheDir = File(envPath, "cache")
            
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            
            return cacheDir
        } catch (e: Exception) {
            Log.e(TAG, "Error getting environment cache directory", e)
            return null
        }
    }

    /**
     * Gets the files directory for an environment
     *
     * @param environmentId The ID of the environment
     * @return The files directory, or null if not found
     */
    fun getEnvironmentFilesDir(environmentId: String): File? {
        try {
            val envPath = getEnvironmentPath(environmentId) ?: return null
            val filesDir = File(envPath, "files")
            
            if (!filesDir.exists()) {
                filesDir.mkdirs()
            }
            
            return filesDir
        } catch (e: Exception) {
            Log.e(TAG, "Error getting environment files directory", e)
            return null
        }
    }

    /**
     * Gets the databases directory for an environment
     *
     * @param environmentId The ID of the environment
     * @return The databases directory, or null if not found
     */
    fun getEnvironmentDatabasesDir(environmentId: String): File? {
        try {
            val envPath = getEnvironmentPath(environmentId) ?: return null
            val databasesDir = File(envPath, "databases")
            
            if (!databasesDir.exists()) {
                databasesDir.mkdirs()
            }
            
            return databasesDir
        } catch (e: Exception) {
            Log.e(TAG, "Error getting environment databases directory", e)
            return null
        }
    }

    /**
     * Gets the shared preferences directory for an environment
     *
     * @param environmentId The ID of the environment
     * @return The shared preferences directory, or null if not found
     */
    fun getEnvironmentSharedPrefsDir(environmentId: String): File? {
        try {
            val envPath = getEnvironmentPath(environmentId) ?: return null
            val sharedPrefsDir = File(envPath, "shared_prefs")
            
            if (!sharedPrefsDir.exists()) {
                sharedPrefsDir.mkdirs()
            }
            
            return sharedPrefsDir
        } catch (e: Exception) {
            Log.e(TAG, "Error getting environment shared preferences directory", e)
            return null
        }
    }
}