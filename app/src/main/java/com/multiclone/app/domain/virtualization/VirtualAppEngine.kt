package com.multiclone.app.domain.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core engine that manages virtualization and app cloning
 */
@Singleton
class VirtualAppEngine @Inject constructor(
    private val context: Context,
    private val cloneEnvironment: CloneEnvironment,
    private val clonedAppInstaller: ClonedAppInstaller
) {
    private val TAG = "VirtualAppEngine"

    /**
     * Get all installed applications on the device
     * Filters out system apps and the current app to prevent self-cloning issues
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            val ownPackageName = context.packageName
            
            installedPackages
                .filter { it.packageName != ownPackageName }
                .filter { (it.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0 }
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
                        isSystem = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                    )
                }
                .sortedBy { it.appName.lowercase() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting installed apps", e)
            emptyList()
        }
    }
    
    /**
     * Create a new clone of an app
     */
    suspend fun createClone(
        packageName: String,
        customName: String? = null,
        customIcon: Bitmap? = null,
        cloneIndex: Int = 1
    ): Result<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            // Verify the app exists
            val appInfo = getAppInfo(packageName)
                ?: return@withContext Result.failure(Exception("App not found"))
            
            // Prepare the virtualization environment
            val prepared = cloneEnvironment.prepareEnvironment(packageName, cloneIndex)
            if (!prepared) {
                return@withContext Result.failure(Exception("Failed to prepare clone environment"))
            }
            
            // Extract and install the app
            val installed = clonedAppInstaller.installClonedApp(packageName, cloneIndex)
            if (!installed) {
                return@withContext Result.failure(Exception("Failed to install cloned app"))
            }
            
            // Create the clone info object
            val iconBitmap = customIcon ?: drawableToBitmap(appInfo.icon)
            val clone = CloneInfo(
                packageName = packageName,
                originalAppName = appInfo.appName,
                customName = customName,
                icon = iconBitmap,
                cloneIndex = cloneIndex
            )
            
            Result.success(clone)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating clone", e)
            Result.failure(e)
        }
    }
    
    /**
     * Launch a cloned app
     */
    suspend fun launchClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val intent = Intent().apply {
                action = "com.multiclone.app.LAUNCH_CLONE"
                putExtra("packageName", cloneInfo.packageName)
                putExtra("cloneId", cloneInfo.id)
                putExtra("cloneIndex", cloneInfo.cloneIndex)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error launching clone", e)
            false
        }
    }
    
    /**
     * Create a shortcut for a cloned app
     */
    suspend fun createShortcut(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val shortcutIntent = Intent(Intent.ACTION_VIEW).apply {
                action = "com.multiclone.app.LAUNCH_CLONE"
                putExtra("cloneId", cloneInfo.id)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.parse("multiclone://${cloneInfo.packageName}/${cloneInfo.id}")
            }
            
            val intent = Intent().apply {
                action = Intent.ACTION_CREATE_SHORTCUT
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                putExtra(Intent.EXTRA_SHORTCUT_NAME, cloneInfo.displayName)
                putExtra(Intent.EXTRA_SHORTCUT_ICON, cloneInfo.icon)
            }
            
            context.sendBroadcast(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating shortcut", e)
            false
        }
    }
    
    /**
     * Delete a cloned app
     */
    suspend fun deleteClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            cloneEnvironment.cleanupEnvironment(cloneInfo.packageName, cloneInfo.cloneIndex)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting clone", e)
            false
        }
    }
    
    /**
     * Get information about an installed app
     */
    private suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
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
                isSystem = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app info", e)
            null
        }
    }
    
    /**
     * Convert a drawable to a bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
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