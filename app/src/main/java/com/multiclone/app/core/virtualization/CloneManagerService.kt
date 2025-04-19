package com.multiclone.app.core.virtualization

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main service for managing app clones
 */
@Singleton
class CloneManagerService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine,
    private val cloneEnvironment: CloneEnvironment,
    private val clonedAppInstaller: ClonedAppInstaller
) {
    // Reference to the virtualization service
    private var virtualizationService: VirtualizationService? = null
    
    // Flag indicating if we are bound to the service
    private var isBound = false
    
    // Connection to the virtualization service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("Connected to VirtualizationService")
            val binder = service as VirtualizationService.LocalBinder
            virtualizationService = binder.getService()
            isBound = true
        }
        
        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("Disconnected from VirtualizationService")
            virtualizationService = null
            isBound = false
        }
    }
    
    init {
        Timber.d("CloneManagerService initialized")
        bindToVirtualizationService()
    }
    
    /**
     * Bind to the virtualization service
     */
    private fun bindToVirtualizationService() {
        try {
            Timber.d("Binding to VirtualizationService")
            val intent = Intent(context, VirtualizationService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            Timber.e(e, "Failed to bind to VirtualizationService")
        }
    }
    
    /**
     * Unbind from the virtualization service
     */
    fun unbindFromVirtualizationService() {
        try {
            if (isBound) {
                Timber.d("Unbinding from VirtualizationService")
                context.unbindService(serviceConnection)
                isBound = false
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to unbind from VirtualizationService")
        }
    }
    
    /**
     * Create a new app clone
     */
    suspend fun createClone(packageName: String, cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Creating clone for package: $packageName with ID: ${cloneInfo.id}")
            
            // Create the clone in the repository
            if (!cloneRepository.createClone(cloneInfo)) {
                Timber.e("Failed to create clone in repository")
                return@withContext false
            }
            
            // Set up the virtual environment
            if (!cloneEnvironment.setupEnvironment(cloneInfo)) {
                Timber.e("Failed to set up environment for clone")
                cloneRepository.deleteClone(cloneInfo.id)
                return@withContext false
            }
            
            // Prepare the app's APK
            val apkPath = clonedAppInstaller.prepareCloneApk(packageName, cloneInfo.id)
            if (apkPath == null) {
                Timber.e("Failed to prepare APK for clone")
                cloneEnvironment.cleanupEnvironment(cloneInfo.id)
                cloneRepository.deleteClone(cloneInfo.id)
                return@withContext false
            }
            
            // Create the virtual clone
            val clonePath = virtualAppEngine.createClone(packageName, cloneInfo.id)
            if (clonePath == null) {
                Timber.e("Failed to create virtual clone")
                clonedAppInstaller.cleanupCloneApk(cloneInfo.id)
                cloneEnvironment.cleanupEnvironment(cloneInfo.id)
                cloneRepository.deleteClone(cloneInfo.id)
                return@withContext false
            }
            
            Timber.d("Clone created successfully for package: $packageName with ID: ${cloneInfo.id}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Failed to create clone for package: $packageName")
            
            // Clean up on failure
            try {
                clonedAppInstaller.cleanupCloneApk(cloneInfo.id)
                cloneEnvironment.cleanupEnvironment(cloneInfo.id)
                cloneRepository.deleteClone(cloneInfo.id)
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to clean up after failed clone creation")
            }
            
            return@withContext false
        }
    }
    
    /**
     * Launch a cloned app
     */
    suspend fun launchApp(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Launching app clone: $cloneId")
            
            // Get the clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId)
            if (cloneInfo == null) {
                Timber.e("Clone not found: $cloneId")
                return@withContext false
            }
            
            // Start the clone in the virtualization service
            if (isBound && virtualizationService != null) {
                if (!virtualizationService!!.startClone(cloneId)) {
                    Timber.e("Failed to start clone in virtualization service")
                    return@withContext false
                }
            } else {
                Timber.e("Virtualization service not bound")
                return@withContext false
            }
            
            // Launch the proxy activity
            val intent = CloneProxyActivity.createIntent(context, cloneId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            // Update launch statistics
            cloneRepository.updateLaunchStats(cloneId)
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch app clone: $cloneId")
            return@withContext false
        }
    }
    
    /**
     * Delete a cloned app
     */
    suspend fun deleteClone(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Deleting app clone: $cloneId")
            
            // Stop the clone if it's running
            if (isBound && virtualizationService != null) {
                virtualizationService!!.stopClone(cloneId)
            }
            
            // Clean up the clone's data
            clonedAppInstaller.cleanupCloneApk(cloneId)
            cloneEnvironment.cleanupEnvironment(cloneId)
            
            // Delete the virtual clone
            if (!virtualAppEngine.deleteClone(cloneId)) {
                Timber.e("Failed to delete virtual clone")
                return@withContext false
            }
            
            // Delete the clone from the repository
            if (!cloneRepository.deleteClone(cloneId)) {
                Timber.e("Failed to delete clone from repository")
                return@withContext false
            }
            
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete app clone: $cloneId")
            return@withContext false
        }
    }
    
    /**
     * Get the launch intent for a cloned app
     */
    fun getLaunchIntent(cloneId: String): Intent? {
        try {
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: return null
            
            // Get the launch intent for the original app
            val packageManager = context.packageManager
            val launchIntent = packageManager.getLaunchIntentForPackage(cloneInfo.originalPackageName)
                ?: return null
            
            // Clone the intent and add our clone ID
            val clonedIntent = Intent(launchIntent)
            clonedIntent.putExtra("clone_id", cloneId)
            
            // Add flags to launch in a new task
            clonedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            return clonedIntent
        } catch (e: Exception) {
            Timber.e(e, "Failed to get launch intent for clone: $cloneId")
            return null
        }
    }
}