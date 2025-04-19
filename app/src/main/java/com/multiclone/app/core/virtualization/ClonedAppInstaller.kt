package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Handles installation of cloned apps
 */
class ClonedAppInstaller(private val context: Context) {
    
    companion object {
        const val CLONES_DIRECTORY = "app_clones"
    }
    
    /**
     * Prepares the environment for a cloned app
     * Returns the directory where the clone data will be stored
     */
    fun prepareCloneEnvironment(cloneInfo: CloneInfo): File {
        // Create a dedicated directory for this clone
        val cloneDir = File(context.filesDir, "$CLONES_DIRECTORY/${cloneInfo.id}")
        if (!cloneDir.exists()) {
            cloneDir.mkdirs()
        }
        
        // Create subdirectories for app data
        File(cloneDir, "files").mkdirs()
        File(cloneDir, "cache").mkdirs()
        File(cloneDir, "databases").mkdirs()
        File(cloneDir, "shared_prefs").mkdirs()
        
        return cloneDir
    }
    
    /**
     * Installs a clone of the specified app
     */
    suspend fun installClone(appInfo: AppInfo, cloneInfo: CloneInfo) = withContext(Dispatchers.IO) {
        try {
            // Copy original app's files to the clone directory
            // Note: In a real implementation, this would involve complex APK extraction and modification
            
            // Create a shortcut for the clone
            createShortcut(cloneInfo)
            
            // Start VirtualizationService to manage the clone's lifecycle
            startVirtualizationService(cloneInfo)
            
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Creates a shortcut for the cloned app on the home screen
     */
    private fun createShortcut(cloneInfo: CloneInfo) {
        val shortcutIntent = Intent(context, CloneProxyActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            putExtra("clone_id", cloneInfo.id)
            putExtra("package_name", cloneInfo.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val installShortcutIntent = Intent("com.android.launcher.action.INSTALL_SHORTCUT").apply {
            putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
            putExtra(Intent.EXTRA_SHORTCUT_NAME, cloneInfo.displayName)
            putExtra("duplicate", false)
            
            // Use the original app's icon for now
            // In a real app, we would customize the icon to indicate it's a clone
            val iconUri = Uri.parse("android.resource://${cloneInfo.packageName}/mipmap/ic_launcher")
            putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, android.R.drawable.sym_def_app_icon))
        }
        
        context.sendBroadcast(installShortcutIntent)
    }
    
    /**
     * Starts the virtualization service for a cloned app
     */
    private fun startVirtualizationService(cloneInfo: CloneInfo) {
        val serviceIntent = Intent(context, VirtualizationService::class.java).apply {
            putExtra("clone_id", cloneInfo.id)
            putExtra("package_name", cloneInfo.packageName)
        }
        context.startService(serviceIntent)
    }
}