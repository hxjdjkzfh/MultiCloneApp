package com.multiclone.app.domain.virtualization

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles installation of cloned applications in their isolated environments
 */
@Singleton
class ClonedAppInstaller @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneEnvironment: CloneEnvironment
) {
    private val packageManager = context.packageManager
    
    /**
     * Install an app in the virtual environment
     */
    suspend fun installApp(packageName: String, environmentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Extract the app's APK file
            val appApkFile = extractAppApk(packageName)
            
            // Create a directory for the app in the environment
            val appDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
            appDir.mkdirs()
            
            // Copy the APK to the environment
            val targetApk = File(appDir, "base.apk")
            appApkFile.copyTo(targetApk, overwrite = true)
            
            // Set up app data in the isolated environment
            setupAppData(packageName, environmentId)
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Extract an app's APK file to a temporary location
     */
    private suspend fun extractAppApk(packageName: String): File = withContext(Dispatchers.IO) {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val sourceApk = File(packageInfo.applicationInfo.sourceDir)
        
        // Create a temporary file to store the APK
        val tempApk = File(context.cacheDir, "$packageName.apk")
        sourceApk.copyTo(tempApk, overwrite = true)
        
        tempApk
    }
    
    /**
     * Set up app data in the isolated environment
     */
    private suspend fun setupAppData(packageName: String, environmentId: String) = withContext(Dispatchers.IO) {
        val appDataDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
        
        // Create standard app data directories
        File(appDataDir, "files").mkdirs()
        File(appDataDir, "cache").mkdirs()
        File(appDataDir, "shared_prefs").mkdirs()
        File(appDataDir, "databases").mkdirs()
        
        // Create a .nomedia file to prevent media scanning
        File(appDataDir, ".nomedia").createNewFile()
    }
    
    /**
     * Uninstall an app from the virtual environment
     */
    suspend fun uninstallApp(packageName: String, environmentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val appDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
            appDir.deleteRecursively()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}