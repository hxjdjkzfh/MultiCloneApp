package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles installation, updates, and removal of cloned applications
 * within the virtual environments.
 */
@Singleton
class ClonedAppInstaller @Inject constructor(
    private val context: Context,
    private val cloneEnvironment: CloneEnvironment
) {
    companion object {
        private const val TAG = "ClonedAppInstaller"
        private const val APP_DIR = "app"
    }
    
    /**
     * Installs an app in a virtual environment
     * 
     * @param packageName Package name of the app to install
     * @param cloneId Unique identifier for the clone environment
     * @return Success status of the installation
     */
    suspend fun installApp(packageName: String, cloneId: String): Boolean = 
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Installing app $packageName in clone environment $cloneId")
                
                // Check if app is installed on device
                val packageInfo = try {
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
                    Timber.e(e, "App $packageName is not installed on device")
                    return@withContext false
                }
                
                // Get app's APK path
                val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getApplicationInfo(
                        packageName,
                        PackageManager.ApplicationInfoFlags.of(0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getApplicationInfo(packageName, 0)
                }
                
                val sourceApk = File(appInfo.sourceDir)
                if (!sourceApk.exists()) {
                    Timber.e("APK file not found for $packageName")
                    return@withContext false
                }
                
                // Create app directory in the environment
                val envDir = cloneEnvironment.getEnvironmentDirectory(cloneId)
                val appDir = File(envDir, APP_DIR)
                if (!appDir.exists() && !appDir.mkdirs()) {
                    Timber.e("Failed to create app directory for clone $cloneId")
                    return@withContext false
                }
                
                // Copy the APK to the environment
                val targetApk = File(appDir, "${packageName}.apk")
                sourceApk.inputStream().use { input ->
                    FileOutputStream(targetApk).use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Extract and copy essential data
                setupAppData(packageName, cloneId, appInfo)
                
                // Create app configuration file
                val configFile = File(appDir, "${packageName}.json")
                configFile.writeText("""
                    {
                        "packageName": "$packageName",
                        "versionCode": ${packageInfo.longVersionCode},
                        "versionName": "${packageInfo.versionName}",
                        "installTime": ${System.currentTimeMillis()},
                        "sourceApk": "${sourceApk.absolutePath}"
                    }
                """.trimIndent())
                
                Timber.d("Successfully installed app $packageName in clone environment $cloneId")
                return@withContext true
            } catch (e: Exception) {
                Timber.e(e, "Error installing app $packageName in clone environment $cloneId")
                return@withContext false
            }
        }
    
    /**
     * Updates an app in a virtual environment when the original app is updated
     * 
     * @param packageName Package name of the app to update
     * @param cloneId Unique identifier for the clone environment
     * @return Success status of the update
     */
    suspend fun updateApp(packageName: String, cloneId: String): Boolean = 
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Updating app $packageName in clone environment $cloneId")
                
                // For simplicity, we just reinstall the app
                // In a production implementation, we would also migrate existing data
                return@withContext installApp(packageName, cloneId)
            } catch (e: Exception) {
                Timber.e(e, "Error updating app $packageName in clone environment $cloneId")
                return@withContext false
            }
        }
    
    /**
     * Sets up app data in the clone environment
     */
    private fun setupAppData(
        packageName: String,
        cloneId: String,
        appInfo: ApplicationInfo
    ) {
        try {
            // Create app data directories
            val dataDir = cloneEnvironment.getDataDirectory(cloneId)
            val appDataDir = File(dataDir, packageName)
            if (!appDataDir.exists() && !appDataDir.mkdirs()) {
                Timber.e("Failed to create app data directory for $packageName in clone $cloneId")
                return
            }
            
            // Create common app directories
            File(appDataDir, "shared_prefs").mkdirs()
            File(appDataDir, "databases").mkdirs()
            File(appDataDir, "files").mkdirs()
            File(appDataDir, "cache").mkdirs()
            
            // Copy essential configuration files from original app if needed
            // This is simplified - a real implementation would need more sophisticated logic
            // to selectively copy necessary files without copying sensitive data or user accounts
        } catch (e: Exception) {
            Timber.e(e, "Error setting up app data for $packageName in clone $cloneId")
        }
    }
    
    /**
     * Checks if an app is installed in a specific clone environment
     * 
     * @param packageName Package name to check
     * @param cloneId Clone environment ID
     * @return True if the app is installed in the clone environment
     */
    fun isAppInstalled(packageName: String, cloneId: String): Boolean {
        val envDir = cloneEnvironment.getEnvironmentDirectory(cloneId)
        val appApk = File(File(envDir, APP_DIR), "${packageName}.apk")
        return appApk.exists() && appApk.length() > 0
    }
}