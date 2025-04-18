package com.multiclone.app.domain.virtualization

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.zip.ZipFile
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
    private val TAG = "ClonedAppInstaller"
    private val packageManager = context.packageManager
    
    /**
     * Install an app in the virtual environment
     */
    suspend fun installApp(packageName: String, environmentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Installing $packageName in environment $environmentId")
            
            // Get package info to extract metadata
            val packageInfo = packageManager.getPackageInfo(packageName, 
                PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES)
            
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
            
            // Extract and store app metadata for the virtualization layer
            storeAppMetadata(packageInfo, environmentId)
            
            // Extract and store app resources that might be needed
            extractAppResources(packageInfo, environmentId)
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error installing app", e)
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
        val tempDir = File(context.cacheDir, "app_extracts").apply { mkdirs() }
        val tempApk = File(tempDir, "$packageName-${System.currentTimeMillis()}.apk")
        
        // Copy the APK
        sourceApk.copyTo(tempApk, overwrite = true)
        
        // Calculate and log the APK checksum for integrity verification
        val md5sum = calculateMD5(tempApk)
        Log.d(TAG, "Extracted $packageName APK (MD5: $md5sum)")
        
        tempApk
    }
    
    /**
     * Set up app data in the isolated environment
     */
    private suspend fun setupAppData(packageName: String, environmentId: String) = withContext(Dispatchers.IO) {
        val appDataDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
        
        // Create standard app data directories
        val standardDirs = listOf(
            "files",
            "cache",
            "shared_prefs",
            "databases",
            "app_webview",
            "code_cache",
            "no_backup",
            "lib"
        )
        
        standardDirs.forEach { dirName ->
            File(appDataDir, dirName).apply {
                if (!exists()) mkdirs()
            }
        }
        
        // Create a .nomedia file to prevent media scanning
        File(appDataDir, ".nomedia").createNewFile()
        
        // Create default preferences file if it doesn't exist
        val prefsDir = File(appDataDir, "shared_prefs")
        if (!File(prefsDir, "$packageName"+"_preferences.xml").exists()) {
            prefsDir.mkdirs()
            File(prefsDir, "$packageName"+"_preferences.xml").writeText(
                """<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
                <map>
                    <string name="multiclone_environment_id">$environmentId</string>
                    <long name="multiclone_installation_time">${System.currentTimeMillis()}</long>
                </map>""".trimIndent()
            )
        }
    }
    
    /**
     * Store app metadata for the virtualization layer
     */
    private suspend fun storeAppMetadata(packageInfo: PackageInfo, environmentId: String) = withContext(Dispatchers.IO) {
        val packageName = packageInfo.packageName
        val appDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
        
        // Create metadata directory
        val metadataDir = File(appDir, "metadata")
        metadataDir.mkdirs()
        
        // Extract important metadata
        val metadata = JSONObject().apply {
            put("packageName", packageName)
            put("versionName", packageInfo.versionName ?: "")
            put("versionCode", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) 
                packageInfo.longVersionCode else packageInfo.versionCode.toLong())
            put("appName", packageManager.getApplicationLabel(packageInfo.applicationInfo).toString())
            put("environmentId", environmentId)
            put("installTime", System.currentTimeMillis())
            put("lastUpdateTime", packageInfo.lastUpdateTime)
            
            // Store flags
            put("flags", packageInfo.applicationInfo.flags)
            put("targetSdkVersion", packageInfo.applicationInfo.targetSdkVersion)
            
            // Store main activity if available
            packageManager.getLaunchIntentForPackage(packageName)?.component?.let { component ->
                put("mainActivity", component.className)
            }
            
            // Store permission info
            val permissionsArray = packageInfo.requestedPermissions
            if (permissionsArray != null) {
                put("requestedPermissions", permissionsArray.joinToString(","))
            }
        }
        
        // Write metadata to file
        File(metadataDir, "app_info.json").writeText(metadata.toString(2))
        
        // Create a copy of app icon in the environment
        saveAppIcon(packageInfo.applicationInfo, metadataDir)
    }
    
    /**
     * Save app icon to the environment
     */
    private fun saveAppIcon(appInfo: ApplicationInfo, metadataDir: File) {
        try {
            // Get app icon drawable
            val icon = packageManager.getApplicationIcon(appInfo)
            
            // Convert drawable to bitmap
            val bitmap = Bitmap.createBitmap(
                icon.intrinsicWidth, 
                icon.intrinsicHeight, 
                Bitmap.Config.ARGB_8888
            )
            
            val canvas = Canvas(bitmap)
            icon.setBounds(0, 0, canvas.width, canvas.height)
            icon.draw(canvas)
            
            // Save bitmap to file
            val iconFile = File(metadataDir, "app_icon.png")
            FileOutputStream(iconFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            Log.d(TAG, "Saved app icon to ${iconFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving app icon", e)
        }
    }
    
    /**
     * Extract important resources from the app APK
     */
    private suspend fun extractAppResources(packageInfo: PackageInfo, environmentId: String) = withContext(Dispatchers.IO) {
        val packageName = packageInfo.packageName
        val appDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
        val resourcesDir = File(appDir, "resources").apply { mkdirs() }
        
        // The actual APK file
        val apkFile = File(packageInfo.applicationInfo.sourceDir)
        
        try {
            // We'll use ZipFile to extract specific files from the APK
            // that are needed for virtualization
            ZipFile(apkFile).use { zip ->
                // Extract AndroidManifest.xml and resources.arsc if available
                extractZipEntry(zip, "AndroidManifest.xml", File(resourcesDir, "AndroidManifest.xml"))
                extractZipEntry(zip, "resources.arsc", File(resourcesDir, "resources.arsc"))
                
                // You could extract other important resources here as needed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting resources from APK", e)
        }
    }
    
    /**
     * Helper function to extract a file from a zip archive
     */
    private fun extractZipEntry(zip: ZipFile, entryName: String, outputFile: File): Boolean {
        return try {
            val entry = zip.getEntry(entryName)
            if (entry != null) {
                zip.getInputStream(entry).use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting $entryName", e)
            false
        }
    }
    
    /**
     * Calculate MD5 hash of a file
     */
    private fun calculateMD5(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        return file.inputStream().use { inputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
            md.digest().joinToString("") { "%02x".format(it) }
        }
    }
    
    /**
     * Uninstall an app from the virtual environment
     */
    suspend fun uninstallApp(packageName: String, environmentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Uninstalling $packageName from environment $environmentId")
            
            val appDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
            appDir.deleteRecursively()
            
            // Clean up any temporary files
            val tempFile = File(context.cacheDir, "$packageName.apk")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error uninstalling app", e)
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Get metadata for an installed app clone
     */
    suspend fun getAppMetadata(packageName: String, environmentId: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val appDir = cloneEnvironment.getAppDataDir(environmentId, packageName)
            val metadataFile = File(appDir, "metadata/app_info.json")
            
            if (metadataFile.exists()) {
                JSONObject(metadataFile.readText())
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app metadata", e)
            null
        }
    }
}