package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles the installation of cloned apps
 */
@Singleton
class ClonedAppInstaller @Inject constructor(
    private val context: Context
) {
    // Root directory for storing APKs
    private val apkStorageDir: File by lazy {
        File(context.filesDir, "apks")
    }
    
    init {
        // Ensure the APK storage directory exists
        if (!apkStorageDir.exists()) {
            apkStorageDir.mkdirs()
        }
    }
    
    /**
     * Prepare the clone APK
     * This extracts the original APK and prepares it for virtualization
     */
    fun prepareCloneApk(packageName: String, cloneId: String): String? {
        try {
            Timber.d("Preparing clone APK for package: $packageName with ID: $cloneId")
            
            // Get the original app info
            val packageInfo = getPackageInfo(packageName) ?: return null
            
            // Create a directory for the clone's APK
            val cloneApkDir = File(apkStorageDir, cloneId)
            if (!cloneApkDir.exists()) {
                cloneApkDir.mkdirs()
            }
            
            // Extract the original APK
            val originalApkPath = packageInfo.applicationInfo.sourceDir
            val originalApkFile = File(originalApkPath)
            
            // Copy the APK to our clone directory
            val cloneApkFile = File(cloneApkDir, "base.apk")
            if (!copyFile(originalApkFile, cloneApkFile)) {
                Timber.e("Failed to copy APK for clone: $cloneId")
                return null
            }
            
            // Create a metadata file for the clone
            val metadataFile = File(cloneApkDir, "metadata.json")
            val metadata = """
                {
                    "originalPackage": "$packageName",
                    "cloneId": "$cloneId",
                    "originalApkPath": "$originalApkPath",
                    "versionCode": ${packageInfo.versionCode},
                    "versionName": "${packageInfo.versionName}",
                    "lastUpdated": ${System.currentTimeMillis()}
                }
            """.trimIndent()
            
            metadataFile.writeText(metadata)
            
            Timber.d("Successfully prepared APK for clone: $cloneId")
            return cloneApkFile.absolutePath
        } catch (e: Exception) {
            Timber.e(e, "Failed to prepare APK for clone: $cloneId")
            return null
        }
    }
    
    /**
     * Clean up the clone's APK files
     */
    fun cleanupCloneApk(cloneId: String): Boolean {
        try {
            Timber.d("Cleaning up APK for clone: $cloneId")
            
            val cloneApkDir = File(apkStorageDir, cloneId)
            if (!cloneApkDir.exists()) {
                Timber.w("APK directory doesn't exist for clone: $cloneId")
                return true
            }
            
            // Recursively delete the APK directory
            return cloneApkDir.deleteRecursively()
        } catch (e: Exception) {
            Timber.e(e, "Failed to clean up APK for clone: $cloneId")
            return false
        }
    }
    
    /**
     * Get the APK path for a clone
     */
    fun getCloneApkPath(cloneId: String): String? {
        val apkFile = File(File(apkStorageDir, cloneId), "base.apk")
        return if (apkFile.exists()) apkFile.absolutePath else null
    }
    
    /**
     * Copy a file from source to destination
     */
    private fun copyFile(sourceFile: File, destFile: File): Boolean {
        try {
            sourceFile.inputStream().use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to copy file from ${sourceFile.absolutePath} to ${destFile.absolutePath}")
            return false
        }
    }
    
    /**
     * Get package info for an app
     */
    private fun getPackageInfo(packageName: String): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e("Package not found: $packageName")
            null
        }
    }
}