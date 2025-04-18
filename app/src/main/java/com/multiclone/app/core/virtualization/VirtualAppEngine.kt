package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.utils.IconUtils
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core engine for app virtualization functionality
 * Handles creating and managing virtual environments for cloned apps
 */
@Singleton
class VirtualAppEngine @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val CLONE_PREFIX = "com.multiclone.app.clone_"
        private const val VIRTUAL_ENV_DIR = "virtual_environments"
    }

    /**
     * Get a list of installed apps that can be cloned
     */
    fun getInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val installedApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }

        return installedApps
            .filter { pkg -> 
                // Filter out system apps and our own app
                val isNotSystemApp = pkg.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
                val isNotOurApp = !pkg.packageName.startsWith("com.multiclone.app")
                isNotSystemApp && isNotOurApp
            }
            .map { pkg -> mapToAppInfo(pkg, packageManager) }
            .sortedBy { it.appName }
    }

    /**
     * Creates a virtual environment for a cloned app
     */
    fun createClone(
        packageName: String, 
        displayName: String,
        customIcon: Bitmap?,
        cloneId: String = UUID.randomUUID().toString()
    ): CloneInfo {
        // Get original app info
        val packageManager = context.packageManager
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
        
        val appInfo = mapToAppInfo(packageInfo, packageManager)
        
        // Create virtual environment directory
        val virtualEnvDir = getCloneEnvironmentDirectory(cloneId)
        virtualEnvDir.mkdirs()
        
        // Save customized icon if provided
        val iconBitmap = customIcon ?: IconUtils.drawableToBitmap(appInfo.icon)
        val iconFile = File(virtualEnvDir, "icon.png")
        IconUtils.saveBitmapToFile(iconBitmap, iconFile)
        
        // Create and return clone info
        return CloneInfo(
            id = cloneId,
            packageName = packageName,
            originalAppName = appInfo.appName,
            displayName = displayName,
            customIcon = iconBitmap,
            virtualEnvironmentId = cloneId,
            creationTimestamp = System.currentTimeMillis(),
            lastUsedTimestamp = System.currentTimeMillis()
        )
    }

    /**
     * Launch a cloned app using CloneProxyActivity
     */
    fun launchClone(cloneInfo: CloneInfo) {
        val intent = Intent(context, CloneProxyActivity::class.java).apply {
            putExtra("packageName", cloneInfo.packageName)
            putExtra("cloneId", cloneInfo.id)
            putExtra("displayName", cloneInfo.displayName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        
        // Update last used timestamp
        cloneInfo.lastUsedTimestamp = System.currentTimeMillis()
    }

    /**
     * Delete a cloned app's virtual environment
     */
    fun deleteClone(cloneInfo: CloneInfo): Boolean {
        val virtualEnvDir = getCloneEnvironmentDirectory(cloneInfo.virtualEnvironmentId)
        return if (virtualEnvDir.exists()) {
            virtualEnvDir.deleteRecursively()
        } else {
            false
        }
    }

    /**
     * Get the virtual environment directory for a clone
     */
    private fun getCloneEnvironmentDirectory(cloneId: String): File {
        val baseDir = File(context.filesDir, VIRTUAL_ENV_DIR)
        return File(baseDir, cloneId)
    }

    /**
     * Map PackageInfo to our AppInfo data model
     */
    private fun mapToAppInfo(packageInfo: PackageInfo, packageManager: PackageManager): AppInfo {
        val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
        val icon = packageInfo.applicationInfo.loadIcon(packageManager)
        val isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        
        return AppInfo(
            packageName = packageInfo.packageName,
            appName = appName,
            versionName = packageInfo.versionName,
            icon = icon,
            isSystemApp = isSystemApp
        )
    }
}