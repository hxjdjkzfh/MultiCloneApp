package com.multiclone.app.core.virtualization

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core engine for managing virtual app instances.
 * Acts as the central coordinator for cloning and running virtualized apps.
 */
@Singleton
class VirtualAppEngine @Inject constructor(
    private val context: Context,
    private val cloneRepository: CloneRepository,
    private val cloneEnvironment: CloneEnvironment,
    private val clonedAppInstaller: ClonedAppInstaller
) {
    // Current state of running clones
    private val runningClones = mutableMapOf<String, CloneInfo>()
    
    /**
     * Initialize the virtualization engine
     */
    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            Timber.d("Initializing VirtualAppEngine")
            
            // Perform environment cleanup and validation
            validateCloneEnvironments()
            
            // Start the VirtualizationService to handle background operations
            startVirtualizationService()
        }
    }
    
    /**
     * Get list of installed apps that can be cloned
     */
    suspend fun getCloneableApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            Timber.d("Getting cloneable apps")
            
            val packageManager = context.packageManager
            val installedPackages = packageManager.getInstalledPackages(0)
            
            val appInfoList = mutableListOf<AppInfo>()
            
            for (packageInfo in installedPackages) {
                try {
                    val appInfo = packageInfo.applicationInfo
                    val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    
                    // Create AppInfo object
                    val app = AppInfo(
                        packageName = packageInfo.packageName,
                        appName = packageManager.getApplicationLabel(appInfo).toString(),
                        versionName = packageInfo.versionName ?: "",
                        versionCode = packageInfo.longVersionCode,
                        isSystemApp = isSystemApp,
                        icon = packageManager.getApplicationIcon(appInfo),
                        firstInstallTime = packageInfo.firstInstallTime,
                        lastUpdateTime = packageInfo.lastUpdateTime
                    )
                    
                    // Only add if cloneable
                    if (app.isCloneable()) {
                        appInfoList.add(app)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error processing package ${packageInfo.packageName}")
                }
            }
            
            appInfoList.sortedBy { it.appName }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get cloneable apps")
            emptyList()
        }
    }
    
    /**
     * Create a new app clone
     */
    suspend fun createClone(appInfo: AppInfo, customName: String, isolationLevel: Int = 1, 
                           addToLauncher: Boolean = true): CloneInfo? = withContext(Dispatchers.IO) {
        try {
            Timber.d("Creating clone for ${appInfo.packageName}")
            
            // Create the clone info
            val cloneInfo = CloneInfo(
                originalPackageName = appInfo.packageName,
                customName = customName.ifEmpty { appInfo.appName },
                storageIsolationLevel = isolationLevel,
                showInLauncher = addToLauncher
            )
            
            // Prepare the cloned app (file system, shortcuts, etc.)
            if (!clonedAppInstaller.prepareClonedApp(cloneInfo)) {
                Timber.e("Failed to prepare cloned app")
                return@withContext null
            }
            
            // Save the clone info to the repository
            if (!cloneRepository.addClone(cloneInfo)) {
                Timber.e("Failed to save clone info")
                cloneEnvironment.cleanupEnvironment(cloneInfo.id)
                return@withContext null
            }
            
            // Return the created clone info
            cloneInfo
        } catch (e: Exception) {
            Timber.e(e, "Failed to create clone")
            null
        }
    }
    
    /**
     * Update an existing clone
     */
    suspend fun updateClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Updating clone ${cloneInfo.id}")
            
            // Update the cloned app
            if (!clonedAppInstaller.updateClonedApp(cloneInfo)) {
                Timber.e("Failed to update cloned app")
                return@withContext false
            }
            
            // Update the clone info in the repository
            if (!cloneRepository.updateClone(cloneInfo)) {
                Timber.e("Failed to update clone info")
                return@withContext false
            }
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to update clone")
            false
        }
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Deleting clone $cloneId")
            
            // Get the clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId)
            if (cloneInfo == null) {
                Timber.e("Clone not found: $cloneId")
                return@withContext false
            }
            
            // If the clone is running, stop it
            if (runningClones.containsKey(cloneId)) {
                stopApp(cloneInfo)
            }
            
            // Uninstall the cloned app
            if (!clonedAppInstaller.uninstallClonedApp(cloneInfo)) {
                Timber.e("Failed to uninstall cloned app")
                return@withContext false
            }
            
            // Remove the clone info from the repository
            if (!cloneRepository.deleteClone(cloneId)) {
                Timber.e("Failed to delete clone info")
                return@withContext false
            }
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete clone")
            false
        }
    }
    
    /**
     * Launch a cloned app
     */
    suspend fun launchApp(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Launching app ${cloneInfo.id}")
            
            // Verify that the original app is installed
            if (!clonedAppInstaller.isOriginalAppInstalled(cloneInfo.originalPackageName)) {
                Timber.e("Original app not installed: ${cloneInfo.originalPackageName}")
                return@withContext false
            }
            
            // Verify that the clone environment exists
            if (!cloneEnvironment.isEnvironmentValid(cloneInfo.id)) {
                Timber.e("Clone environment invalid: ${cloneInfo.id}")
                return@withContext false
            }
            
            // Update the running state
            cloneRepository.updateCloneRunningStatus(cloneInfo.id, true)
            runningClones[cloneInfo.id] = cloneInfo
            
            // Get the original app's launch intent
            val packageManager = context.packageManager
            val launchIntent = packageManager.getLaunchIntentForPackage(cloneInfo.originalPackageName)
            
            if (launchIntent == null) {
                Timber.e("No launch intent found for ${cloneInfo.originalPackageName}")
                return@withContext false
            }
            
            // Add virtualization parameters
            val virtualizedIntent = Intent(launchIntent).apply {
                // Add clone ID to identify which environment to use
                putExtra("clone_id", cloneInfo.id)
                
                // Flag to indicate this is a virtualized launch
                putExtra("virtualized", true)
                
                // Add flags for new task
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                
                // Set the component to launch through our proxy
                setClassName(
                    context.packageName,
                    "com.multiclone.app.core.virtualization.CloneProxyActivity"
                )
            }
            
            // Start the virtualized app via the proxy activity
            context.startActivity(virtualizedIntent)
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch app")
            false
        }
    }
    
    /**
     * Stop a running cloned app
     */
    suspend fun stopApp(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Stopping app ${cloneInfo.id}")
            
            // Remove from running clones list
            runningClones.remove(cloneInfo.id)
            
            // Update the running state
            cloneRepository.updateCloneRunningStatus(cloneInfo.id, false)
            
            // The actual app process termination would be handled by the system 
            // or we would need to use Android's ActivityManager for force-stop
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop app")
            false
        }
    }
    
    /**
     * Get a list of running clones
     */
    fun getRunningClones(): List<CloneInfo> {
        return runningClones.values.toList()
    }
    
    /**
     * Check if a clone is running
     */
    fun isCloneRunning(cloneId: String): Boolean {
        return runningClones.containsKey(cloneId)
    }
    
    /**
     * Validate all clone environments
     */
    private suspend fun validateCloneEnvironments() {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Validating clone environments")
                
                // Get all clones
                val clones = cloneRepository.getClones().value
                
                // Check if original apps are still installed
                for (clone in clones) {
                    if (!clonedAppInstaller.isOriginalAppInstalled(clone.originalPackageName)) {
                        Timber.w("Original app not installed for clone ${clone.id}: ${clone.originalPackageName}")
                        // We keep the clone in case the app is reinstalled later
                    }
                    
                    // Check if environment needs update
                    if (clone.needsEnvironmentUpdate(2)) {
                        Timber.d("Clone ${clone.id} needs environment update")
                        // We would perform environment migration here
                        // For now, just update the version
                        cloneRepository.updateCloneEnvironmentVersion(clone.id)
                    }
                    
                    // Verify environment exists
                    if (!cloneEnvironment.isEnvironmentValid(clone.id)) {
                        Timber.w("Clone environment invalid for ${clone.id}, recreating")
                        cloneEnvironment.initializeEnvironment(clone)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to validate clone environments")
            }
        }
    }
    
    /**
     * Start the virtualization service
     */
    private fun startVirtualizationService() {
        try {
            Timber.d("Starting VirtualizationService")
            
            val serviceIntent = Intent(context, VirtualizationService::class.java)
            context.startService(serviceIntent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start VirtualizationService")
        }
    }
    
    /**
     * Open a file from a clone environment
     */
    fun openFileFromClone(cloneId: String, filePath: String, mimeType: String): Boolean {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Timber.e("File does not exist: $filePath")
                return false
            }
            
            // Get content URI using FileProvider
            val uri = clonedAppInstaller.getFileProviderUri(cloneId, file)
            
            // Create intent to view the file
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to open file")
            return false
        }
    }
    
    /**
     * Share a file from a clone environment
     */
    fun shareFileFromClone(cloneId: String, filePath: String, mimeType: String, title: String): Boolean {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Timber.e("File does not exist: $filePath")
                return false
            }
            
            // Get content URI using FileProvider
            val uri = clonedAppInstaller.getFileProviderUri(cloneId, file)
            
            // Create intent to share the file
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to share file")
            return false
        }
    }
}