package com.multiclone.app.core.virtualization

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Service for managing cloned apps.
 * Provides a binding interface for the UI to interact with the virtualization system.
 */
@AndroidEntryPoint
class CloneManagerService : Service() {
    
    // Binder given to clients
    private val binder = LocalBinder()
    
    // Coroutine scope for background operations
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    inner class LocalBinder : Binder() {
        fun getService(): CloneManagerService = this@CloneManagerService
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("CloneManagerService - onCreate")
        
        // Initialize the virtualization engine
        serviceScope.launch {
            virtualAppEngine.initialize()
        }
    }
    
    override fun onDestroy() {
        Timber.d("CloneManagerService - onDestroy")
        super.onDestroy()
    }
    
    /**
     * Get all clones
     */
    fun getClones(): Flow<List<CloneInfo>> {
        return cloneRepository.getClones()
    }
    
    /**
     * Get all cloneable apps
     */
    suspend fun getCloneableApps(): List<AppInfo> {
        return virtualAppEngine.getCloneableApps()
    }
    
    /**
     * Create a new clone
     */
    suspend fun createClone(appInfo: AppInfo, customName: String, isolationLevel: Int = 1,
                           addToLauncher: Boolean = true): CloneInfo? {
        return virtualAppEngine.createClone(appInfo, customName, isolationLevel, addToLauncher)
    }
    
    /**
     * Update an existing clone
     */
    suspend fun updateClone(cloneInfo: CloneInfo): Boolean {
        return virtualAppEngine.updateClone(cloneInfo)
    }
    
    /**
     * Delete a clone
     */
    suspend fun deleteClone(cloneId: String): Boolean {
        return virtualAppEngine.deleteClone(cloneId)
    }
    
    /**
     * Launch a cloned app
     */
    suspend fun launchApp(cloneId: String): Boolean {
        val cloneInfo = cloneRepository.getCloneById(cloneId) ?: return false
        return virtualAppEngine.launchApp(cloneInfo)
    }
    
    /**
     * Get a specific clone by ID
     */
    suspend fun getCloneById(cloneId: String): CloneInfo? {
        return cloneRepository.getCloneById(cloneId)
    }
    
    /**
     * Get clones by original package name
     */
    suspend fun getClonesByPackage(packageName: String): List<CloneInfo> {
        return cloneRepository.getClonesByPackage(packageName)
    }
    
    /**
     * Check if a clone is running
     */
    fun isCloneRunning(cloneId: String): Boolean {
        return virtualAppEngine.isCloneRunning(cloneId)
    }
    
    /**
     * Get all running clones
     */
    fun getRunningClones(): List<CloneInfo> {
        return virtualAppEngine.getRunningClones()
    }
    
    /**
     * Stop a running cloned app
     */
    suspend fun stopApp(cloneId: String): Boolean {
        val cloneInfo = cloneRepository.getCloneById(cloneId) ?: return false
        return virtualAppEngine.stopApp(cloneInfo)
    }
    
    /**
     * Open a file from a clone environment
     */
    fun openFileFromClone(cloneId: String, filePath: String, mimeType: String): Boolean {
        return virtualAppEngine.openFileFromClone(cloneId, filePath, mimeType)
    }
    
    /**
     * Share a file from a clone environment
     */
    fun shareFileFromClone(cloneId: String, filePath: String, mimeType: String, title: String): Boolean {
        return virtualAppEngine.shareFileFromClone(cloneId, filePath, mimeType, title)
    }
}