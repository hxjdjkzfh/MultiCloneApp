package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.multiclone.app.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing installed applications on the device.
 */
@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps
    
    /**
     * Loads all installed applications on the device.
     * Filters out system apps by default.
     */
    suspend fun loadInstalledApps(includeSystemApps: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            
            val appInfoList = packages.mapNotNull { packageInfo ->
                // Skip system apps if not requested
                if (!includeSystemApps && isSystemApp(packageInfo.applicationInfo)) {
                    return@mapNotNull null
                }
                
                // Get basic app information
                val packageName = packageInfo.packageName
                val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                val versionName = packageInfo.versionName ?: "1.0"
                val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                }
                val isSystemApp = isSystemApp(packageInfo.applicationInfo)
                val icon = packageInfo.applicationInfo.loadIcon(packageManager)
                val installTime = packageInfo.firstInstallTime
                
                // Determine if the app can be cloned
                val isCloneable = AppInfo.isPackageCloneable(packageName)
                val nonCloneableReason = if (!isCloneable) {
                    "System apps cannot be cloned for security reasons."
                } else {
                    null
                }
                
                // Create AppInfo object
                AppInfo(
                    packageName = packageName,
                    appName = appName,
                    versionName = versionName,
                    versionCode = versionCode,
                    isSystemApp = isSystemApp,
                    icon = icon,
                    installTimeMillis = installTime,
                    isCloneable = isCloneable,
                    nonCloneableReason = nonCloneableReason
                )
            }.sortedBy { it.appName.lowercase() }
            
            // Update state flow with loaded apps
            _installedApps.value = appInfoList
            Timber.d("Loaded ${appInfoList.size} installed apps")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load installed apps")
            _installedApps.value = emptyList()
        }
    }
    
    /**
     * Gets details for a specific app by package name.
     */
    suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            
            val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            val versionName = packageInfo.versionName ?: "1.0"
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
            val isSystemApp = isSystemApp(packageInfo.applicationInfo)
            val icon = packageInfo.applicationInfo.loadIcon(packageManager)
            val installTime = packageInfo.firstInstallTime
            
            val isCloneable = AppInfo.isPackageCloneable(packageName)
            val nonCloneableReason = if (!isCloneable) {
                "System apps cannot be cloned for security reasons."
            } else {
                null
            }
            
            AppInfo(
                packageName = packageName,
                appName = appName,
                versionName = versionName,
                versionCode = versionCode,
                isSystemApp = isSystemApp,
                icon = icon,
                installTimeMillis = installTime,
                isCloneable = isCloneable,
                nonCloneableReason = nonCloneableReason
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to get info for package: $packageName")
            null
        }
    }
    
    /**
     * Checks if an application is a system app.
     */
    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }
    
    /**
     * Gets a filtered list of apps that can be cloned.
     */
    fun getCloneableApps(): List<AppInfo> {
        return _installedApps.value.filter { it.isCloneable }
    }
    
    /**
     * Launches an application by package name.
     * Returns true if successful, false otherwise.
     */
    fun launchApp(packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                Timber.w("No launch intent found for package: $packageName")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch app: $packageName")
            false
        }
    }
}