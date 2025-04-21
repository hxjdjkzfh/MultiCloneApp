package com.multiclone.app.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.multiclone.app.domain.models.ClonedApp
import com.multiclone.app.domain.models.InstalledApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core engine for app virtualization and cloning.
 * Handles app cloning, launching, and management.
 */
@Singleton
class VirtualAppEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneManager: CloneManager,
    private val storageManager: VirtualStorageManager
) {
    /**
     * Get a list of installed applications that can be cloned
     */
    suspend fun getCloneableApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedApps = mutableListOf<InstalledApp>()
        
        // Get all installed packages on the device
        val installedPackages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(
                PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        }
        
        // Process each package
        for (packageInfo in installedPackages) {
            try {
                // Skip packages without a launch activity (not launchable apps)
                val launchIntent = packageManager.getLaunchIntentForPackage(packageInfo.packageName)
                if (launchIntent == null) {
                    continue
                }
                
                // Skip system packages
                val isSystemApp = (packageInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                
                // Skip our own app
                if (packageInfo.packageName == context.packageName) {
                    continue
                }
                
                // Create app info
                val appInfo = InstalledApp(
                    packageName = packageInfo.packageName,
                    appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
                    versionName = packageInfo.versionName ?: "",
                    versionCode = PackageInfoCompat.getLongVersionCode(packageInfo),
                    isSystemApp = isSystemApp,
                    cloneCount = cloneManager.getCloneCount(packageInfo.packageName),
                    installedTime = packageInfo.firstInstallTime,
                    lastUpdateTime = packageInfo.lastUpdateTime
                )
                
                installedApps.add(appInfo)
            } catch (e: Exception) {
                Timber.e(e, "Error processing package: ${packageInfo.packageName}")
            }
        }
        
        // Sort by app name
        installedApps.sortBy { it.appName.lowercase() }
        
        Timber.d("Found ${installedApps.size} cloneable apps")
        return@withContext installedApps
    }
    
    /**
     * Create a clone of an app
     * @param packageName The package name of the app to clone
     * @param displayName User-defined name for the clone
     * @param isolateStorage Whether to isolate storage for this clone
     * @return The created clone's ID if successful, null otherwise
     */
    suspend fun createClone(
        packageName: String, 
        displayName: String,
        isolateStorage: Boolean = true
    ): String? = withContext(Dispatchers.IO) {
        try {
            // Generate a unique clone ID
            val cloneId = UUID.randomUUID().toString()
            
            // Determine clone index (for distinguishing multiple clones of the same app)
            val cloneIndex = cloneManager.getCloneCount(packageName)
            
            // Create app clone data
            val clonedApp = ClonedApp(
                cloneId = cloneId,
                originalPackageName = packageName,
                displayName = displayName,
                storageIsolated = isolateStorage,
                cloneIndex = cloneIndex
            )
            
            // Prepare virtual storage if needed
            if (isolateStorage) {
                val storageCreated = storageManager.createIsolatedStorage(packageName, cloneId)
                if (!storageCreated) {
                    Timber.e("Failed to create isolated storage for $packageName:$cloneId")
                    return@withContext null
                }
            }
            
            // Save the clone icon
            saveCloneIcon(packageName, cloneId)
            
            // Register the clone
            cloneManager.registerClone(clonedApp)
            
            Timber.d("Created clone for $packageName: $cloneId")
            return@withContext cloneId
        } catch (e: Exception) {
            Timber.e(e, "Error creating clone for $packageName")
            return@withContext null
        }
    }
    
    /**
     * Delete a clone
     * @param cloneId The ID of the clone to delete
     * @return True if deletion was successful
     */
    suspend fun deleteClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val clone = cloneManager.getClone(cloneId) ?: return@withContext false
            
            // Clean up storage if needed
            if (clone.storageIsolated) {
                storageManager.removeIsolatedStorage(clone.originalPackageName, cloneId)
            }
            
            // Remove clone from registry
            cloneManager.unregisterClone(cloneId)
            
            Timber.d("Deleted clone: $cloneId")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error deleting clone: $cloneId")
            return@withContext false
        }
    }
    
    /**
     * Launch a cloned app
     * @param cloneId The ID of the clone to launch
     * @return True if launch was successful
     */
    fun launchClone(cloneId: String): Boolean {
        try {
            val clone = cloneManager.getClone(cloneId)
            if (clone == null) {
                Timber.e("Attempted to launch non-existent clone: $cloneId")
                return false
            }
            
            // Update last used time
            cloneManager.updateLastUsed(cloneId)
            
            // Create launch intent
            val launchIntent = Intent(context, CloneProxyActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_CLONE_ID, cloneId)
                putExtra(EXTRA_PACKAGE_NAME, clone.originalPackageName)
            }
            
            // Start the proxy activity which will handle virtualization
            context.startActivity(launchIntent)
            
            Timber.d("Launched clone: $cloneId (${clone.displayName})")
            return true
        } catch (e: Exception) {
            Timber.e(e, "Error launching clone: $cloneId")
            return false
        }
    }
    
    /**
     * Save an icon for the cloned app
     */
    private fun saveCloneIcon(packageName: String, cloneId: String): String? {
        try {
            val packageManager = context.packageManager
            val drawable = packageManager.getApplicationIcon(packageName)
            
            // Convert drawable to bitmap
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, 
                drawable.intrinsicHeight, 
                Bitmap.Config.ARGB_8888
            )
            
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            
            // Save bitmap to file
            val iconFile = File(context.filesDir, "clone_icons/${cloneId}.png")
            iconFile.parentFile?.mkdirs()
            
            FileOutputStream(iconFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            return iconFile.absolutePath
        } catch (e: Exception) {
            Timber.e(e, "Error saving clone icon for $packageName:$cloneId")
            return null
        }
    }
    
    /**
     * Get an app's drawable
     */
    fun getAppDrawable(packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            Timber.e(e, "Error getting app drawable for $packageName")
            null
        }
    }
    
    companion object {
        // Intent extras
        const val EXTRA_CLONE_ID = "com.multiclone.app.extra.CLONE_ID"
        const val EXTRA_PACKAGE_NAME = "com.multiclone.app.extra.PACKAGE_NAME"
    }
}