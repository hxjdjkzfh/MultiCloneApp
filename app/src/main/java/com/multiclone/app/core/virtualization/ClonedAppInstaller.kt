package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.multiclone.app.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles installation of cloned apps
 */
@Singleton
class ClonedAppInstaller @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Get a list of all installed apps that can be cloned
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(0)
        
        return@withContext installedPackages
            .filter { packageInfo ->
                // Filter out system apps and our own app
                val isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                val isOurApp = packageInfo.packageName == context.packageName
                
                !isSystemApp && !isOurApp && packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null
            }
            .map { packageInfo ->
                val appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
                val icon = packageManager.getApplicationIcon(packageInfo.packageName)
                val sourceDir = packageInfo.applicationInfo.sourceDir
                val isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                
                AppInfo(
                    packageName = packageInfo.packageName,
                    appName = appName,
                    icon = icon,
                    sourceDir = sourceDir,
                    isSystem = isSystemApp
                )
            }
            .sortedBy { it.appName.lowercase() }
    }
    
    /**
     * Get icon for a package
     */
    fun getAppIcon(packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    /**
     * Get app info for a package
     */
    fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()
            val icon = packageManager.getApplicationIcon(packageName)
            val sourceDir = applicationInfo.sourceDir
            val isSystemApp = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            
            AppInfo(
                packageName = packageName,
                appName = appName,
                icon = icon,
                sourceDir = sourceDir,
                isSystem = isSystemApp
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if a package is installed
     */
    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}