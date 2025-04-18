package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.multiclone.app.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Get the list of installed applications on the device.
     *
     * @param onlyUserApps If true, returns only user-installed apps, excluding system apps.
     * @return List of AppInfo objects representing installed applications.
     */
    suspend fun getInstalledApps(onlyUserApps: Boolean = true): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedPackages: List<PackageInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }
        
        val appsList = mutableListOf<AppInfo>()
        
        for (packageInfo in installedPackages) {
            val isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            
            // Skip system apps if onlyUserApps is true
            if (onlyUserApps && isSystemApp) {
                continue
            }
            
            try {
                val appInfo = packageInfo.applicationInfo
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val appIcon = drawable2Bitmap(packageManager.getApplicationIcon(appInfo))
                
                val appSizeInBytes = calculateAppSize(packageInfo.packageName)
                val appSizeInMB = appSizeInBytes / (1024f * 1024f)
                
                appsList.add(
                    AppInfo(
                        packageName = packageInfo.packageName,
                        name = appName,
                        icon = appIcon,
                        versionName = packageInfo.versionName ?: "",
                        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            packageInfo.longVersionCode
                        } else {
                            @Suppress("DEPRECATION")
                            packageInfo.versionCode.toLong()
                        },
                        sizeInMB = appSizeInMB,
                        isSystem = isSystemApp,
                        installTime = packageInfo.firstInstallTime,
                        lastUpdateTime = packageInfo.lastUpdateTime
                    )
                )
            } catch (e: Exception) {
                // Skip apps that cannot be accessed
                e.printStackTrace()
            }
        }
        
        // Sort by name
        appsList.sortedBy { it.name }
    }
    
    /**
     * Get information about a specific app by its package name.
     *
     * @param packageName The package name of the app to get information for.
     * @return AppInfo object representing the requested app, or null if app not found.
     */
    suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            
            val appInfo = packageInfo.applicationInfo
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val appIcon = drawable2Bitmap(packageManager.getApplicationIcon(appInfo))
            
            val appSizeInBytes = calculateAppSize(packageName)
            val appSizeInMB = appSizeInBytes / (1024f * 1024f)
            
            AppInfo(
                packageName = packageInfo.packageName,
                name = appName,
                icon = appIcon,
                versionName = packageInfo.versionName ?: "",
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                },
                sizeInMB = appSizeInMB,
                isSystem = appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                installTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Calculates the size of an installed application.
     * 
     * @param packageName The package name of the app to calculate the size for.
     * @return The size of the app in bytes.
     */
    private fun calculateAppSize(packageName: String): Long {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            
            var size: Long = 0
            size += File(applicationInfo.sourceDir).length()
            
            if (applicationInfo.splitSourceDirs != null) {
                for (splitSourceDir in applicationInfo.splitSourceDirs!!) {
                    size += File(splitSourceDir).length()
                }
            }
            
            size
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
    
    /**
     * Converts a Drawable to a Bitmap.
     *
     * @param drawable The Drawable to convert.
     * @return The Bitmap created from the Drawable.
     */
    private fun drawable2Bitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        
        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
