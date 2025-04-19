package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VirtualAppEngine using virtualization techniques to
 * create and manage cloned apps.
 */
@Singleton
class VirtualAppEngineImpl @Inject constructor(
    private val context: Context,
    private val cloneEnvironment: CloneEnvironment,
    private val clonedAppInstaller: ClonedAppInstaller,
    private val proxyActivityManager: CloneProxyActivityManager
) : VirtualAppEngine {

    // Root directory for all clones
    private val clonesRootDir by lazy {
        File(context.filesDir, "virtual_apps").apply {
            if (!exists() && !mkdirs()) {
                Timber.e("Failed to create virtual apps directory")
            }
        }
    }
    
    /**
     * Installs a clone of the specified package.
     */
    override suspend fun installClone(packageName: String, cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Installing clone of $packageName with ID ${cloneInfo.id}")
            
            // Check if the original app is installed
            if (!isOriginalAppInstalled(packageName)) {
                Timber.e("Original app $packageName is not installed")
                return@withContext false
            }
            
            // Create clone directory
            val cloneDir = File(clonesRootDir, cloneInfo.id)
            if (!cloneDir.exists() && !cloneDir.mkdirs()) {
                Timber.e("Failed to create clone directory for ${cloneInfo.id}")
                return@withContext false
            }
            
            // Initialize clone environment
            if (!cloneEnvironment.initialize(cloneDir, cloneInfo)) {
                Timber.e("Failed to initialize clone environment for ${cloneInfo.id}")
                return@withContext false
            }
            
            // Install app in the virtual environment
            if (!clonedAppInstaller.install(packageName, cloneDir, cloneInfo)) {
                Timber.e("Failed to install app $packageName in virtual environment")
                return@withContext false
            }
            
            // Register proxy activities
            if (!proxyActivityManager.registerProxies(packageName, cloneInfo)) {
                Timber.e("Failed to register proxy activities for $packageName")
                return@withContext false
            }
            
            Timber.d("Successfully installed clone of $packageName with ID ${cloneInfo.id}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error installing clone of $packageName")
            return@withContext false
        }
    }

    /**
     * Launches a cloned app.
     */
    override suspend fun launchClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Launching clone ${cloneInfo.id} (${cloneInfo.packageName})")
            
            // Check if the clone is installed
            if (!isCloneInstalled(cloneInfo)) {
                Timber.e("Clone ${cloneInfo.id} is not installed")
                return@withContext false
            }
            
            // Get the launch intent through the proxy manager
            val launchIntent = proxyActivityManager.getLaunchIntent(cloneInfo)
            
            if (launchIntent == null) {
                Timber.e("Could not get launch intent for clone ${cloneInfo.id}")
                return@withContext false
            }
            
            // Add flags to start a new task
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            // Launch the app
            context.startActivity(launchIntent)
            
            Timber.d("Successfully launched clone ${cloneInfo.id}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error launching clone ${cloneInfo.id}")
            return@withContext false
        }
    }

    /**
     * Removes a cloned app.
     */
    override suspend fun removeClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Removing clone ${cloneInfo.id} (${cloneInfo.packageName})")
            
            // Unregister proxy activities
            proxyActivityManager.unregisterProxies(cloneInfo)
            
            // Clean up clone environment
            cloneEnvironment.cleanup(cloneInfo)
            
            // Delete clone directory
            val cloneDir = File(clonesRootDir, cloneInfo.id)
            if (cloneDir.exists()) {
                if (!deleteRecursively(cloneDir)) {
                    Timber.w("Could not completely delete clone directory for ${cloneInfo.id}")
                }
            }
            
            Timber.d("Successfully removed clone ${cloneInfo.id}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error removing clone ${cloneInfo.id}")
            return@withContext false
        }
    }

    /**
     * Checks if a clone is installed.
     */
    override suspend fun isCloneInstalled(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val cloneDir = File(clonesRootDir, cloneInfo.id)
            return@withContext cloneDir.exists() && cloneEnvironment.isValid(cloneDir, cloneInfo)
        } catch (e: Exception) {
            Timber.e(e, "Error checking if clone ${cloneInfo.id} is installed")
            return@withContext false
        }
    }

    /**
     * Updates settings for an existing clone.
     */
    override suspend fun updateCloneSettings(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Updating settings for clone ${cloneInfo.id}")
            
            // Check if the clone is installed
            if (!isCloneInstalled(cloneInfo)) {
                Timber.e("Clone ${cloneInfo.id} is not installed")
                return@withContext false
            }
            
            // Update clone environment settings
            val cloneDir = File(clonesRootDir, cloneInfo.id)
            val success = cloneEnvironment.updateSettings(cloneDir, cloneInfo)
            
            if (success) {
                Timber.d("Successfully updated settings for clone ${cloneInfo.id}")
            } else {
                Timber.e("Failed to update settings for clone ${cloneInfo.id}")
            }
            
            return@withContext success
        } catch (e: Exception) {
            Timber.e(e, "Error updating settings for clone ${cloneInfo.id}")
            return@withContext false
        }
    }
    
    /**
     * Checks if the original app is installed on the device.
     */
    private suspend fun isOriginalAppInstalled(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            
            return@withContext true
        } catch (e: Exception) {
            return@withContext false
        }
    }
    
    /**
     * Recursively deletes a directory and all its contents.
     */
    private fun deleteRecursively(fileOrDirectory: File): Boolean {
        try {
            if (fileOrDirectory.isDirectory) {
                fileOrDirectory.listFiles()?.forEach { child ->
                    deleteRecursively(child)
                }
            }
            return fileOrDirectory.delete()
        } catch (e: IOException) {
            Timber.e(e, "Error deleting ${fileOrDirectory.absolutePath}")
            return false
        }
    }
}