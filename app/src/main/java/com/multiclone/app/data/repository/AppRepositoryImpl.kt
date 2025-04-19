package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.multiclone.app.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AppRepository for accessing installed applications on the device.
 */
@Singleton
class AppRepositoryImpl @Inject constructor(
    private val context: Context
) : AppRepository {
    /**
     * Gets a list of all installed (non-system) apps on the device
     * 
     * @return List of app information objects
     */
    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            Timber.d("Getting installed apps")
            
            val packageManager = context.packageManager
            val installedApps = mutableListOf<AppInfo>()
            
            // Get all installed packages
            val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledPackages(0)
            }
            
            // Filter and convert to AppInfo objects
            packages.forEach { packageInfo ->
                // Check if it's a regular app (skip system apps)
                val isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                
                // Skip system apps unless specifically requested
                if (!isSystemApp) {
                    try {
                        val appInfo = AppInfo(
                            packageName = packageInfo.packageName,
                            appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
                            versionName = packageInfo.versionName ?: "",
                            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                packageInfo.longVersionCode
                            } else {
                                @Suppress("DEPRECATION")
                                packageInfo.versionCode.toLong()
                            },
                            isSystemApp = isSystemApp,
                            installTime = packageInfo.firstInstallTime,
                            lastUpdateTime = packageInfo.lastUpdateTime,
                            appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
                        )
                        
                        installedApps.add(appInfo)
                    } catch (e: Exception) {
                        Timber.e(e, "Error processing app ${packageInfo.packageName}")
                    }
                }
            }
            
            Timber.d("Found ${installedApps.size} installed (non-system) apps")
            return@withContext installedApps.sortedBy { it.appName.lowercase() }
        } catch (e: Exception) {
            Timber.e(e, "Error getting installed apps")
            return@withContext emptyList()
        }
    }
    
    /**
     * Gets app info for a single package
     * 
     * @param packageName The package name to look up
     * @return App info or null if not found or error
     */
    override suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            Timber.d("Getting app info for $packageName")
            
            val packageManager = context.packageManager
            
            // Get package info
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            
            // Create AppInfo object
            val isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            
            return@withContext AppInfo(
                packageName = packageInfo.packageName,
                appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
                versionName = packageInfo.versionName ?: "",
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                },
                isSystemApp = isSystemApp,
                installTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime,
                appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
            )
        } catch (e: Exception) {
            Timber.e(e, "Error getting app info for $packageName")
            return@withContext null
        }
    }
    
    /**
     * Checks if an app is installed on the device
     * 
     * @param packageName The package name to check
     * @return True if the app is installed
     */
    override suspend fun isAppInstalled(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Checking if app $packageName is installed")
            
            val packageManager = context.packageManager
            
            // Try to get the app info
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            
            // If no exception was thrown, the app is installed
            return@withContext true
        } catch (e: Exception) {
            Timber.d("App $packageName is not installed")
            return@withContext false
        }
    }
}