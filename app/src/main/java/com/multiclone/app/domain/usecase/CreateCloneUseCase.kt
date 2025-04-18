package com.multiclone.app.domain.usecase

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.CloneEnvironment
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import com.multiclone.app.utils.IconUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for creating a new app clone
 */
class CreateCloneUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine,
    private val cloneEnvironment: CloneEnvironment,
    private val iconUtils: IconUtils
) {
    private val TAG = "CreateCloneUseCase"
    private val packageManager = context.packageManager
    
    /**
     * Create a new clone of an app
     * @param packageName The package name of the app to clone
     * @param cloneName A custom name for the clone
     * @param customIcon Optional custom icon for the clone
     * @param badgeNumber Optional badge number to show on the icon
     * @return The created CloneInfo wrapped in Result
     */
    suspend operator fun invoke(
        packageName: String,
        cloneName: String,
        customIcon: Bitmap? = null,
        badgeNumber: String? = "2"
    ): Result<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating clone for $packageName with name '$cloneName'")
            
            // Verify app exists and is installed
            if (!isAppInstalled(packageName)) {
                Log.e(TAG, "App $packageName is not installed")
                return@withContext Result.failure(Exception("App not installed or not found"))
            }
            
            // Get app info
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            
            // Generate a unique ID for this clone
            val cloneId = UUID.randomUUID().toString()
            
            Log.d(TAG, "Generated clone ID: $cloneId")
            
            // Process icon - if no custom icon provided, use app icon with badge
            val processedIcon = when {
                customIcon != null -> {
                    Log.d(TAG, "Using custom icon for clone")
                    customIcon
                }
                badgeNumber != null -> {
                    Log.d(TAG, "Adding badge $badgeNumber to app icon")
                    val appIcon = iconUtils.getAppIcon(packageName)
                    if (appIcon != null) {
                        iconUtils.drawBadge(appIcon, badgeNumber)
                    } else {
                        Log.w(TAG, "Could not get app icon, using null")
                        null
                    }
                }
                else -> {
                    Log.d(TAG, "Using original app icon without badge")
                    iconUtils.getAppIcon(packageName)
                }
            }
            
            // Save clone info
            val cloneInfo = cloneRepository.saveClone(
                id = cloneId,
                packageName = packageName,
                originalAppName = appName,
                cloneName = cloneName,
                customIcon = processedIcon,
                badgeNumber = badgeNumber
            )
            
            Log.d(TAG, "Clone info saved, creating virtual environment")
            
            // Create virtual environment
            val environmentResult = virtualAppEngine.createVirtualEnvironment(
                packageName = packageName,
                cloneId = cloneId
            )
            
            if (environmentResult.isSuccess) {
                // Get the environment ID
                val environmentId = environmentResult.getOrNull()
                Log.d(TAG, "Virtual environment created successfully with ID: $environmentId")
                
                // Update clone with environment ID if it's different from clone ID
                if (environmentId != null && environmentId != cloneId) {
                    Log.d(TAG, "Updating clone with environment ID")
                    cloneRepository.updateEnvironmentId(cloneId, environmentId)
                }
                
                // Successfully created clone and environment
                Result.success(cloneInfo)
            } else {
                // Failed to create environment, clean up
                Log.e(TAG, "Failed to create virtual environment", environmentResult.exceptionOrNull())
                cloneRepository.deleteClone(cloneId)
                Result.failure(environmentResult.exceptionOrNull() 
                    ?: Exception("Failed to create virtual environment"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating clone", e)
            Result.failure(e)
        }
    }
    
    /**
     * Check if an app can be cloned (validates package and permissions)
     * @param packageName The package name to check
     * @return True if the app can be cloned, false otherwise
     */
    suspend fun canCloneApp(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Check if app is installed
            if (!isAppInstalled(packageName)) {
                Log.d(TAG, "App $packageName is not installed")
                return@withContext false
            }
            
            // Check if virtualization is supported
            if (!virtualAppEngine.isVirtualizationSupported()) {
                Log.d(TAG, "Virtualization not supported on this device")
                return@withContext false
            }
            
            // Check if it's a system app (you might want to disallow cloning system apps)
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val isSystemApp = appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            
            // Some apps might not be clonable due to technical limitations
            val isBlacklistedApp = isAppInBlacklist(packageName)
            
            // Get count of existing clones for this package
            val existingClones = cloneRepository.getClonesByPackage(packageName)
            val hasReachedCloneLimit = existingClones.size >= MAX_CLONES_PER_APP
            
            // Return true only if all conditions pass
            !isSystemApp && !isBlacklistedApp && !hasReachedCloneLimit
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if app can be cloned", e)
            false
        }
    }
    
    /**
     * Check if an app is installed on the device
     */
    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * Check if an app is in the blacklist of non-clonable apps
     */
    private fun isAppInBlacklist(packageName: String): Boolean {
        // List of apps that cannot be cloned (for technical or security reasons)
        val blacklist = listOf(
            context.packageName, // Can't clone our own app
            "com.android.systemui",
            "com.android.settings"
            // Add other non-clonable apps here
        )
        
        return blacklist.contains(packageName) || packageName.startsWith("com.android.") ||
               packageName.startsWith("com.google.android.")
    }
    
    companion object {
        private const val MAX_CLONES_PER_APP = 5 // Maximum allowed clones per app
    }
}