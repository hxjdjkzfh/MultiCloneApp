package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ClonedAppInstaller that handles installation
 * of cloned applications in virtual environments.
 */
@Singleton
class ClonedAppInstallerImpl @Inject constructor(
    private val context: Context
) : ClonedAppInstaller {

    companion object {
        private const val APP_DATA_DIR = "app_data"
        private const val APP_CODE_DIR = "app_code"
        private const val APP_LIB_DIR = "app_lib"
        private const val MANIFEST_FILE = "manifest.json"
    }

    /**
     * Installs a cloned app in the specified directory.
     */
    override suspend fun install(packageName: String, cloneDir: File, cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Installing cloned app $packageName in ${cloneDir.path}")
            
            // Create app-specific directories in the clone environment
            val appDataDir = File(cloneDir, APP_DATA_DIR)
            val appCodeDir = File(cloneDir, APP_CODE_DIR)
            val appLibDir = File(cloneDir, APP_LIB_DIR)
            
            arrayOf(appDataDir, appCodeDir, appLibDir).forEach { dir ->
                if (!dir.exists() && !dir.mkdirs()) {
                    Timber.e("Failed to create app directory: ${dir.path}")
                    return@withContext false
                }
            }
            
            // Get app information
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(
                        (PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES).toLong()
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES
                )
            }
            
            // Extract application metadata
            val applicationInfo = packageInfo.applicationInfo
            
            // Create manifest file with app information
            val manifestFile = File(cloneDir, MANIFEST_FILE)
            val manifest = buildManifest(packageName, cloneInfo, packageInfo)
            manifestFile.writeText(manifest)
            
            // Copy application resources if needed
            // For virtual app execution, we typically don't need actual app binaries
            // as we'll be using proxy activities, but we can copy app-specific resources if needed
            
            // For this implementation, we're using a virtualization approach that doesn't require
            // copying the actual APK files, which would need root access.
            // Instead, we create a virtual environment and use proxy activities to launch the original app
            // in an isolated manner.
            
            // Create an empty placeholder file to indicate installation is complete
            val installFlagFile = File(cloneDir, ".installed")
            installFlagFile.createNewFile()
            
            Timber.d("Successfully installed cloned app $packageName in ${cloneDir.path}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error installing cloned app $packageName")
            return@withContext false
        }
    }
    
    /**
     * Builds a JSON manifest with app and clone information.
     */
    private fun buildManifest(packageName: String, cloneInfo: CloneInfo, packageInfo: PackageManager.PackageInfo): String {
        val manifestBuilder = StringBuilder()
        manifestBuilder.append("{\n")
        manifestBuilder.append("  \"packageName\": \"$packageName\",\n")
        manifestBuilder.append("  \"cloneId\": \"${cloneInfo.id}\",\n")
        manifestBuilder.append("  \"originalAppName\": \"${cloneInfo.originalAppName}\",\n")
        manifestBuilder.append("  \"cloneName\": \"${cloneInfo.cloneName ?: cloneInfo.originalAppName}\",\n")
        manifestBuilder.append("  \"versionName\": \"${packageInfo.versionName ?: ""}\",\n")
        manifestBuilder.append("  \"versionCode\": ${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode},\n")
        manifestBuilder.append("  \"installTime\": ${System.currentTimeMillis()},\n")
        manifestBuilder.append("  \"isNotificationsEnabled\": ${cloneInfo.isNotificationsEnabled}\n")
        manifestBuilder.append("}")
        return manifestBuilder.toString()
    }
}