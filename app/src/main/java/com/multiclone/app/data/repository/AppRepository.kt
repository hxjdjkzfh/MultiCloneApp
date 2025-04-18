package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.utils.IconUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to handle operations related to installed applications
 */
@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val iconUtils: IconUtils
) {
    private val packageManager = context.packageManager
    
    /**
     * Get a list of all non-system apps installed on the device
     */
    fun getInstalledApps(): Flow<List<AppInfo>> = flow {
        val installedApps = mutableListOf<AppInfo>()
        
        // Get all installed packages
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }
        
        // Process each package
        for (packageInfo in packages) {
            // Skip system apps
            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                continue
            }
            
            // Create AppInfo object
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
                icon = iconUtils.getAppIcon(packageInfo.packageName),
                isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                installTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime
            )
            
            installedApps.add(appInfo)
        }
        
        // Sort by app name
        installedApps.sortBy { it.appName.lowercase() }
        
        emit(installedApps)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get app information for a specific package
     */
    suspend fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            
            AppInfo(
                packageName = packageInfo.packageName,
                appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
                versionName = packageInfo.versionName ?: "",
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                },
                icon = iconUtils.getAppIcon(packageInfo.packageName),
                isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                installTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime
            )
        } catch (e: Exception) {
            null
        }
    }
}