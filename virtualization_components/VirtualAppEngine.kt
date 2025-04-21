package com.multiclone.app.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.Keep
import com.multiclone.app.domain.models.ClonedApp
import com.multiclone.app.domain.models.InstalledApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main engine responsible for app virtualization and isolation.
 * This is the core component that handles the entire virtualization process.
 */
@Keep
@Singleton
class VirtualAppEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneManager: CloneManager,
    private val storageManager: VirtualStorageManager
) {
    private val packageManager: PackageManager = context.packageManager
    
    /**
     * Get a list of all installed apps that can be cloned
     */
    suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        try {
            val installedApps = mutableListOf<InstalledApp>()
            
            // Get all installed apps
            val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledApplications(0)
            }
            
            // For each installed app, check if it can be cloned
            packages.forEach { appInfo ->
                if (isCloneable(appInfo)) {
                    val cloneCount = cloneManager.getCloneCount(appInfo.packageName)
                    
                    installedApps.add(
                        InstalledApp(
                            packageName = appInfo.packageName,
                            appName = getAppName(appInfo),
                            versionName = getAppVersionName(appInfo.packageName),
                            versionCode = getAppVersionCode(appInfo.packageName),
                            appIcon = getAppIcon(appInfo.packageName),
                            isSystemApp = isSystemApp(appInfo),
                            hasExistingClones = cloneCount > 0,
                            cloneCount = cloneCount,
                            supportsMultipleUsers = true, // Assume all apps support multiple users
                            hasSpecialPermissions = hasSpecialPermissions(appInfo)
                        )
                    )
                }
            }
            
            // Sort by app name
            installedApps.sortedBy { it.appName }
        } catch (e: Exception) {
            Timber.e(e, "Error getting installed apps")
            emptyList()
        }
    }
    
    /**
     * Get details for a specific app
     */
    suspend fun getAppDetails(packageName: String): InstalledApp = withContext(Dispatchers.IO) {
        try {
            // Get app info
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            
            val cloneCount = cloneManager.getCloneCount(packageName)
            
            InstalledApp(
                packageName = appInfo.packageName,
                appName = getAppName(appInfo),
                versionName = getAppVersionName(packageName),
                versionCode = getAppVersionCode(packageName),
                appIcon = getAppIcon(packageName),
                isSystemApp = isSystemApp(appInfo),
                hasExistingClones = cloneCount > 0,
                cloneCount = cloneCount,
                supportsMultipleUsers = true,
                hasSpecialPermissions = hasSpecialPermissions(appInfo)
            )
        } catch (e: Exception) {
            Timber.e(e, "Error getting app details for $packageName")
            throw e
        }
    }
    
    /**
     * Get all cloned apps
     */
    suspend fun getClonedApps(): List<ClonedApp> = withContext(Dispatchers.IO) {
        cloneManager.getAllClones()
    }
    
    /**
     * Create a new clone of an app
     */
    suspend fun createClone(
        packageName: String,
        cloneId: String = UUID.randomUUID().toString(),
        cloneName: String,
        storageIsolated: Boolean = true,
        notificationsEnabled: Boolean = true
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get app info
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            
            // Create isolated storage if needed
            if (storageIsolated) {
                storageManager.createIsolatedStorage(packageName, cloneId)
            }
            
            // Register the clone
            val clonedApp = ClonedApp(
                originalPackageName = packageName,
                originalAppName = getAppName(appInfo),
                cloneId = cloneId,
                cloneName = cloneName,
                storageIsolated = storageIsolated,
                notificationsEnabled = notificationsEnabled,
                isRunning = false,
                lastUsed = System.currentTimeMillis(),
                customIconColor = null,
                customBadge = null,
                appIcon = getAppIcon(packageName)
            )
            
            cloneManager.registerClone(clonedApp)
            
            // Create shortcut if possible
            createShortcut(clonedApp)
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Error creating clone for $packageName")
            false
        }
    }
    
    /**
     * Launch a cloned app
     */
    fun launchClone(cloneId: String): Boolean {
        try {
            val clonedApp = cloneManager.getClone(cloneId) ?: return false
            
            // Create intent to launch via proxy
            val launchIntent = Intent(context, CloneProxyActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                putExtra(EXTRA_CLONE_ID, cloneId)
                putExtra(EXTRA_PACKAGE_NAME, clonedApp.originalPackageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Start the proxy activity
            context.startActivity(launchIntent)
            
            // Update last used time
            cloneManager.updateLastUsed(cloneId)
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Error launching clone $cloneId")
            return false
        }
    }
    
    /**
     * Remove a cloned app
     */
    suspend fun removeClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val clone = cloneManager.getClone(cloneId) ?: return@withContext false
            
            // Remove isolated storage if needed
            if (clone.storageIsolated) {
                storageManager.removeIsolatedStorage(clone.originalPackageName, cloneId)
            }
            
            // Unregister the clone
            cloneManager.unregisterClone(cloneId)
            
            // Remove shortcut if possible
            removeShortcut(clone)
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Error removing clone $cloneId")
            false
        }
    }
    
    /**
     * Create a shortcut for a cloned app
     */
    private fun createShortcut(clonedApp: ClonedApp) {
        // Implementation will depend on the Android version
        // For Android 8+, we'd use ShortcutManager
        // For older versions, we'd use broadcast
    }
    
    /**
     * Remove a shortcut for a cloned app
     */
    private fun removeShortcut(clonedApp: ClonedApp) {
        // Implementation will depend on the Android version
    }
    
    // Helper methods
    
    private fun isCloneable(appInfo: ApplicationInfo): Boolean {
        // Skip system apps
        if (isSystemApp(appInfo) && !isCriticalApp(appInfo)) {
            return false
        }
        
        // Skip our own app
        if (appInfo.packageName == context.packageName) {
            return false
        }
        
        return true
    }
    
    private fun isSystemApp(appInfo: ApplicationInfo): Boolean {
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }
    
    private fun isCriticalApp(appInfo: ApplicationInfo): Boolean {
        // Define packages that are critical and shouldn't be cloned
        val criticalPackages = setOf(
            "android",
            "com.android.systemui",
            "com.google.android.gms",
            context.packageName
        )
        
        return criticalPackages.contains(appInfo.packageName)
    }
    
    private fun hasSpecialPermissions(appInfo: ApplicationInfo): Boolean {
        // Check for special permissions that might be problematic
        return false
    }
    
    private fun getAppName(appInfo: ApplicationInfo): String {
        return packageManager.getApplicationLabel(appInfo).toString()
    }
    
    private fun getAppVersionName(packageName: String): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0)).versionName ?: ""
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0).versionName ?: ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun getAppVersionCode(packageName: String): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                }
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
            }
        } catch (e: Exception) {
            0
        }
    }
    
    private fun getAppIcon(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }
    
    companion object {
        const val EXTRA_CLONE_ID = "extra_clone_id"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }
}