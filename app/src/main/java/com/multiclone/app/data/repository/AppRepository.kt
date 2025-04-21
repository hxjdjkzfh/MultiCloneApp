package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.multiclone.app.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Repository interface for getting information about installed apps.
 */
interface AppRepository {
    /**
     * Get a list of all installed apps.
     * 
     * @return List of AppInfo objects
     */
    suspend fun getInstalledApps(): List<AppInfo>
    
    /**
     * Get information about a specific app by package name.
     * 
     * @param packageName The package name of the app
     * @return AppInfo for the specified package, or null if not found
     */
    suspend fun getAppInfo(packageName: String): AppInfo
}

/**
 * Implementation of AppRepository that uses PackageManager to get app information.
 * 
 * @param context Android context
 */
class AppRepositoryImpl(private val context: Context) : AppRepository {
    
    /**
     * Get a list of all installed apps.
     * 
     * @return List of AppInfo objects
     */
    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedApps = mutableListOf<AppInfo>()
        
        try {
            // Get all installed packages
            val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            
            for (packageInfo in packages) {
                val appInfo = packageInfo.applicationInfo
                
                // Skip disabled apps
                if (!appInfo.enabled) continue
                
                // Create AppInfo object
                val app = AppInfo(
                    packageName = packageInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    versionName = packageInfo.versionName ?: "",
                    versionCode = packageInfo.longVersionCode,
                    appIcon = packageManager.getApplicationIcon(appInfo),
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
                
                installedApps.add(app)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading installed apps")
        }
        
        return@withContext installedApps
    }
    
    /**
     * Get information about a specific app by package name.
     * 
     * @param packageName The package name of the app
     * @return AppInfo for the specified package, or null if not found
     * @throws PackageManager.NameNotFoundException if the package is not installed
     */
    override suspend fun getAppInfo(packageName: String): AppInfo = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        
        try {
            val packageInfo = packageManager.getPackageInfo(
                packageName, 
                PackageManager.GET_META_DATA
            )
            val appInfo = packageInfo.applicationInfo
            
            return@withContext AppInfo(
                packageName = packageInfo.packageName,
                appName = packageManager.getApplicationLabel(appInfo).toString(),
                versionName = packageInfo.versionName ?: "",
                versionCode = packageInfo.longVersionCode,
                appIcon = packageManager.getApplicationIcon(appInfo),
                isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            )
        } catch (e: Exception) {
            Timber.e(e, "Error getting app info for package: $packageName")
            throw e
        }
    }
}