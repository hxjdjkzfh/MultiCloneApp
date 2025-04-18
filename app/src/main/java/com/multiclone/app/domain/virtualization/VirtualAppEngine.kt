package com.multiclone.app.domain.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.multiclone.app.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core engine responsible for virtualizing Android applications
 * Manages the isolation, environment setup, and execution of cloned apps
 */
@Singleton
class VirtualAppEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneEnvironment: CloneEnvironment,
    private val clonedAppInstaller: ClonedAppInstaller
) {
    companion object {
        private const val TAG = "VirtualAppEngine"
    }

    /**
     * Creates a new virtual environment for a package
     *
     * @param packageName The package name of the app to virtualize
     * @param cloneId Unique identifier for this clone instance
     * @return Result containing the environment ID if successful
     */
    suspend fun createVirtualEnvironment(
        packageName: String,
        cloneId: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Creating virtual environment for package: $packageName, cloneId: $cloneId")

            // Generate a unique environment ID
            val environmentId = UUID.randomUUID().toString()
            
            // Create isolated environment for the app
            val environmentPath = cloneEnvironment.createEnvironment(environmentId)
            if (environmentPath == null) {
                Log.e(TAG, "Failed to create environment directory")
                return@withContext Result.failure(Exception("Failed to create environment directory"))
            }
            
            Log.d(TAG, "Created environment at: $environmentPath")
            
            // Extract the app's resources and prepare the environment
            val extractResult = clonedAppInstaller.extractAppResources(
                packageName = packageName,
                environmentPath = environmentPath
            )
            
            if (extractResult.isFailure) {
                Log.e(TAG, "Failed to extract app resources: ${extractResult.exceptionOrNull()?.message}")
                return@withContext Result.failure(extractResult.exceptionOrNull() 
                    ?: Exception("Failed to extract app resources"))
            }
            
            // Register the environment ID with the clone
            cloneEnvironment.registerEnvironmentForClone(cloneId, environmentId)
            
            Log.i(TAG, "Successfully created virtual environment with ID: $environmentId")
            
            return@withContext Result.success(environmentId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating virtual environment", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Launches a cloned app in the virtual environment
     *
     * @param cloneId The ID of the clone to launch
     * @param packageName The package name of the original app
     * @return Result indicating success or failure
     */
    suspend fun launchVirtualApp(
        cloneId: String,
        packageName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Launching virtual app: $packageName, cloneId: $cloneId")
            
            // Get the environment ID for this clone
            val environmentId = cloneEnvironment.getEnvironmentIdForClone(cloneId)
            if (environmentId == null) {
                Log.e(TAG, "No environment found for clone: $cloneId")
                return@withContext Result.failure(Exception("No environment found for this clone"))
            }
            
            // Get the launch intent for the original app
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent == null) {
                Log.e(TAG, "No launch intent found for package: $packageName")
                return@withContext Result.failure(Exception("No launch intent found for this app"))
            }
            
            // Prepare the intent for proxying through our CloneProxyActivity
            val proxyIntent = Intent("com.multiclone.app.LAUNCH_CLONE").apply {
                putExtra("clone_id", cloneId)
                putExtra("environment_id", environmentId)
                putExtra("package_name", packageName)
                putExtra("original_intent", launchIntent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Launch the proxy activity
            context.startActivity(proxyIntent)
            
            Log.i(TAG, "Virtual app launched successfully")
            
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching virtual app", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Destroys a virtual environment
     *
     * @param cloneId The ID of the clone to destroy
     * @return Result indicating success or failure
     */
    suspend fun destroyVirtualEnvironment(cloneId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Destroying virtual environment for clone: $cloneId")
            
            // Get the environment ID for this clone
            val environmentId = cloneEnvironment.getEnvironmentIdForClone(cloneId)
            if (environmentId == null) {
                Log.e(TAG, "No environment found for clone: $cloneId")
                return@withContext Result.failure(Exception("No environment found for this clone"))
            }
            
            // Delete the environment directory
            val success = cloneEnvironment.destroyEnvironment(environmentId)
            if (!success) {
                Log.e(TAG, "Failed to destroy environment: $environmentId")
                return@withContext Result.failure(Exception("Failed to destroy environment"))
            }
            
            // Unregister the environment for this clone
            cloneEnvironment.unregisterEnvironmentForClone(cloneId)
            
            Log.i(TAG, "Virtual environment destroyed successfully")
            
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying virtual environment", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Gets information about an installed app
     *
     * @param packageName The package name of the app
     * @return The app information or null if not found
     */
    suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting app info for package: $packageName")
            
            val packageManager = context.packageManager
            
            // Get package info
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(
                        (PackageManager.GET_ACTIVITIES or
                                PackageManager.GET_SERVICES or
                                PackageManager.GET_PROVIDERS or
                                PackageManager.GET_RECEIVERS or
                                PackageManager.GET_META_DATA).toLong()
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_ACTIVITIES or
                            PackageManager.GET_SERVICES or
                            PackageManager.GET_PROVIDERS or
                            PackageManager.GET_RECEIVERS or
                            PackageManager.GET_META_DATA
                )
            }
            
            // Get application info
            val applicationInfo = packageInfo.applicationInfo
            
            // Create app info object
            val appInfo = AppInfo(
                packageName = packageName,
                appName = applicationInfo.loadLabel(packageManager).toString(),
                versionName = packageInfo.versionName ?: "",
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                },
                installTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime,
                isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                appIcon = packageManager.getApplicationIcon(applicationInfo)
            )
            
            Log.d(TAG, "Got app info: ${appInfo.appName} (${appInfo.packageName})")
            
            return@withContext appInfo
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app info", e)
            return@withContext null
        }
    }

    /**
     * Checks if an app can be virtualized
     *
     * @param packageName The package name of the app
     * @return True if the app can be virtualized, false otherwise
     */
    suspend fun canVirtualizeApp(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Checking if app can be virtualized: $packageName")
            
            val packageManager = context.packageManager
            
            // Get package info
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(
                        (PackageManager.GET_PERMISSIONS).toLong()
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS
                )
            }
            
            // Check if this is a system app (we can't virtualize system apps)
            val isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            if (isSystemApp) {
                Log.d(TAG, "Can't virtualize system app: $packageName")
                return@withContext false
            }
            
            // We can virtualize this app
            Log.d(TAG, "App can be virtualized: $packageName")
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if app can be virtualized", e)
            return@withContext false
        }
    }

    /**
     * Gets a list of all installed apps that can be virtualized
     *
     * @return A list of app infos
     */
    suspend fun getVirtualizableApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting list of virtualizable apps")
            
            val packageManager = context.packageManager
            val installedApps = mutableListOf<AppInfo>()
            
            // Get all installed packages
            val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledPackages(0)
            }
            
            // Filter packages
            for (packageInfo in packages) {
                val packageName = packageInfo.packageName
                
                // Skip system apps
                val isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                if (isSystemApp) {
                    continue
                }
                
                // Skip our own app
                if (packageName == context.packageName) {
                    continue
                }
                
                // Get app info
                val appInfo = getAppInfo(packageName)
                if (appInfo != null) {
                    installedApps.add(appInfo)
                }
            }
            
            Log.d(TAG, "Found ${installedApps.size} virtualizable apps")
            
            return@withContext installedApps
        } catch (e: Exception) {
            Log.e(TAG, "Error getting virtualizable apps", e)
            return@withContext emptyList()
        }
    }
}