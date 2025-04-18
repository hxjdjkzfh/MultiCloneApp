package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.multiclone.app.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing information about installed applications
 */
@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Get a list of all installed applications
     * @param includeSystemApps whether to include system apps in the results
     * @return a flow emitting the list of installed apps
     */
    fun getInstalledApps(includeSystemApps: Boolean = false): Flow<List<AppInfo>> = flow {
        val packageManager = context.packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { appInfo ->
                if (!includeSystemApps) {
                    // Only include non-system apps if requested
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                } else {
                    true
                }
            }
            .mapNotNull { appInfo ->
                try {
                    val packageInfo = packageManager.getPackageInfo(appInfo.packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val icon = packageManager.getApplicationIcon(appInfo.packageName)
                    val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    
                    AppInfo(
                        packageName = appInfo.packageName,
                        appName = appName,
                        versionName = packageInfo.versionName,
                        versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            packageInfo.longVersionCode
                        } else {
                            @Suppress("DEPRECATION")
                            packageInfo.versionCode.toLong()
                        },
                        icon = icon,
                        isSystemApp = isSystemApp
                    )
                } catch (e: Exception) {
                    null
                }
            }
            .sortedBy { it.appName.lowercase() }
        
        emit(installedApps)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get information about a specific app by package name
     * @param packageName the package name to query
     * @return the app information or null if not found
     */
    suspend fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val icon = packageManager.getApplicationIcon(packageName)
            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            
            AppInfo(
                packageName = packageName,
                appName = appName,
                versionName = packageInfo.versionName,
                versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                },
                icon = icon,
                isSystemApp = isSystemApp
            )
        } catch (e: Exception) {
            null
        }
    }
}