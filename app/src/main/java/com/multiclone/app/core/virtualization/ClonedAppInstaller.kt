package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import com.multiclone.app.data.model.CloneInfo
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Responsible for installing and updating cloned applications
 */
@Singleton
class ClonedAppInstaller @Inject constructor(
    private val context: Context,
    private val cloneEnvironment: CloneEnvironment
) {
    companion object {
        private const val APP_ICONS_DIR = "app_icons"
        private const val LAUNCHER_SHORTCUTS_DIR = "launcher_shortcuts"
    }
    
    /**
     * Prepare a cloned application
     * This doesn't actually install a separate APK, but sets up the environment
     * and creates necessary launcher shortcuts
     */
    suspend fun prepareClonedApp(cloneInfo: CloneInfo): Boolean {
        try {
            Timber.d("Preparing cloned app for ${cloneInfo.originalPackageName}")
            
            // Get the original app info
            val originalAppInfo = getOriginalAppInfo(cloneInfo.originalPackageName)
                ?: return false
            
            // Initialize the clone environment
            if (!cloneEnvironment.initializeEnvironment(cloneInfo)) {
                Timber.e("Failed to initialize clone environment")
                return false
            }
            
            // Generate and save the app icon
            saveAppIcon(cloneInfo, originalAppInfo)
            
            // Create a launcher shortcut if needed
            if (cloneInfo.showInLauncher) {
                createLauncherShortcut(cloneInfo, originalAppInfo)
            }
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to prepare cloned app")
            return false
        }
    }
    
    /**
     * Update a cloned application
     */
    suspend fun updateClonedApp(cloneInfo: CloneInfo): Boolean {
        try {
            Timber.d("Updating cloned app for ${cloneInfo.originalPackageName}")
            
            // Get the original app info
            val originalAppInfo = getOriginalAppInfo(cloneInfo.originalPackageName)
                ?: return false
            
            // Update the launcher shortcut if needed
            if (cloneInfo.showInLauncher) {
                updateLauncherShortcut(cloneInfo, originalAppInfo)
            }
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to update cloned app")
            return false
        }
    }
    
    /**
     * Uninstall a cloned application
     */
    suspend fun uninstallClonedApp(cloneInfo: CloneInfo): Boolean {
        try {
            Timber.d("Uninstalling cloned app for ${cloneInfo.originalPackageName}")
            
            // Remove launcher shortcut if it exists
            if (cloneInfo.showInLauncher) {
                removeLauncherShortcut(cloneInfo)
            }
            
            // Clean up the environment
            cloneEnvironment.cleanupEnvironment(cloneInfo.id)
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to uninstall cloned app")
            return false
        }
    }
    
    /**
     * Get the original app info
     */
    private fun getOriginalAppInfo(packageName: String): ApplicationInfo? {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get original app info for $packageName")
            null
        }
    }
    
    /**
     * Get the original package info
     */
    private fun getOriginalPackageInfo(packageName: String): PackageInfo? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName, 
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get original package info for $packageName")
            null
        }
    }
    
    /**
     * Save the app icon
     */
    private fun saveAppIcon(cloneInfo: CloneInfo, originalAppInfo: ApplicationInfo): String? {
        try {
            // Get the app icon directory
            val iconsDir = File(context.filesDir, APP_ICONS_DIR)
            if (!iconsDir.exists()) {
                iconsDir.mkdirs()
            }
            
            // Generate a unique filename
            val iconFilename = "${cloneInfo.id}_icon.png"
            val iconFile = File(iconsDir, iconFilename)
            
            // Get the original app icon
            val icon = context.packageManager.getApplicationIcon(originalAppInfo)
            
            // Convert drawable to bitmap and save
            val bitmap = drawableToBitmap(icon)
            FileOutputStream(iconFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            return iconFile.absolutePath
        } catch (e: Exception) {
            Timber.e(e, "Failed to save app icon")
            return null
        }
    }
    
    /**
     * Convert drawable to bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        
        return bitmap
    }
    
    /**
     * Create a launcher shortcut for the cloned app
     */
    private fun createLauncherShortcut(cloneInfo: CloneInfo, originalAppInfo: ApplicationInfo) {
        try {
            // Get the original app label
            val label = context.packageManager.getApplicationLabel(originalAppInfo).toString()
            
            // Create the intent for launching the cloned app
            val launchIntent = Intent(context, CloneProxyActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                putExtra("clone_id", cloneInfo.id)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Create shortcut intent
            val shortcutIntent = Intent("com.android.launcher.action.INSTALL_SHORTCUT").apply {
                putExtra(Intent.EXTRA_SHORTCUT_NAME, cloneInfo.getDisplayName())
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent)
                
                // Use the app icon
                val icon = context.packageManager.getApplicationIcon(originalAppInfo)
                putExtra(Intent.EXTRA_SHORTCUT_ICON, drawableToBitmap(icon))
            }
            
            // Broadcast the shortcut intent
            context.sendBroadcast(shortcutIntent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create launcher shortcut")
        }
    }
    
    /**
     * Update a launcher shortcut for the cloned app
     */
    private fun updateLauncherShortcut(cloneInfo: CloneInfo, originalAppInfo: ApplicationInfo) {
        try {
            // For updating, we need to remove the old shortcut and create a new one
            removeLauncherShortcut(cloneInfo)
            createLauncherShortcut(cloneInfo, originalAppInfo)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update launcher shortcut")
        }
    }
    
    /**
     * Remove a launcher shortcut for the cloned app
     */
    private fun removeLauncherShortcut(cloneInfo: CloneInfo) {
        try {
            // Create the intent for launching the cloned app
            val launchIntent = Intent(context, CloneProxyActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                putExtra("clone_id", cloneInfo.id)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Create uninstall shortcut intent
            val shortcutIntent = Intent("com.android.launcher.action.UNINSTALL_SHORTCUT").apply {
                putExtra(Intent.EXTRA_SHORTCUT_NAME, cloneInfo.getDisplayName())
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent)
            }
            
            // Broadcast the shortcut intent
            context.sendBroadcast(shortcutIntent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove launcher shortcut")
        }
    }
    
    /**
     * Get the URI for sharing files from the clone's environment
     */
    fun getFileProviderUri(cloneId: String, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    /**
     * Check if the original app is still installed
     */
    fun isOriginalAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0) != null
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get the version of the original app
     */
    fun getOriginalAppVersion(packageName: String): Long {
        val packageInfo = getOriginalPackageInfo(packageName) ?: return 0
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }
}