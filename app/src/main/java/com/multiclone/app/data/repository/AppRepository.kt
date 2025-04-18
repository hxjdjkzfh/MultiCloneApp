package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.multiclone.app.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing installed applications data
 */
@Singleton
class AppRepository @Inject constructor(
    private val context: Context
) {
    /**
     * Get all installed applications on the device
     * Filters out the current app (MultiClone) to prevent self-cloning issues
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        val ownPackageName = context.packageName
        
        installedPackages
            .filter { it.packageName != ownPackageName }
            .map { packageInfo ->
                val applicationInfo = packageInfo.applicationInfo
                AppInfo(
                    packageName = packageInfo.packageName,
                    appName = packageManager.getApplicationLabel(applicationInfo).toString(),
                    icon = packageManager.getApplicationIcon(applicationInfo),
                    versionName = packageInfo.versionName ?: "",
                    versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode.toLong()
                    },
                    isSystem = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.appName.lowercase() }
    }

    /**
     * Get app info for a specific package
     */
    suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                packageName, 
                PackageManager.GET_META_DATA
            )
            val applicationInfo = packageInfo.applicationInfo
            
            AppInfo(
                packageName = packageInfo.packageName,
                appName = packageManager.getApplicationLabel(applicationInfo).toString(),
                icon = packageManager.getApplicationIcon(applicationInfo),
                versionName = packageInfo.versionName ?: "",
                versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                },
                isSystem = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            )
        } catch (e: Exception) {
            null
        }
    }
}