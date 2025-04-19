package com.multiclone.app.ui.viewmodels

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.ClonedAppInstaller
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for configuring a new clone
 */
@HiltViewModel
class CloneConfigViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneRepository: CloneRepository
) : ViewModel() {
    
    // App info for the currently selected app
    private val _appInfo = MutableStateFlow<AppInfo?>(null)
    val appInfo: StateFlow<AppInfo?> = _appInfo.asStateFlow()
    
    // Clone name input field
    private val _cloneName = MutableStateFlow("")
    val cloneName: StateFlow<String> = _cloneName.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error message
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Created clone ID
    private val _createdCloneId = MutableStateFlow<String?>(null)
    val createdCloneId: StateFlow<String?> = _createdCloneId.asStateFlow()
    
    /**
     * Load app information for a package name
     */
    fun loadAppInfo(packageName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val appInfo = getAppInfo(packageName)
                _appInfo.value = appInfo
                
                // Initialize clone name with app name
                appInfo?.let { 
                    _cloneName.value = "${it.appName} Clone"
                }
            } catch (e: Exception) {
                _error.value = "Error loading app info: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update the clone name
     */
    fun updateCloneName(name: String) {
        _cloneName.value = name
    }
    
    /**
     * Create a new clone
     */
    fun createClone() {
        val appInfo = _appInfo.value ?: run {
            _error.value = "No app selected"
            return
        }
        
        val cloneName = _cloneName.value.trim()
        if (cloneName.isEmpty()) {
            _error.value = "Clone name cannot be empty"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Create the clone info
                val cloneInfo = CloneInfo(
                    packageName = appInfo.packageName,
                    displayName = cloneName
                )
                
                // Create the virtual environment for this clone
                val installer = ClonedAppInstaller(context)
                val storageDir = installer.prepareCloneEnvironment(cloneInfo)
                
                // Update with the storage path
                val updatedClone = cloneInfo.copy(storagePath = storageDir.absolutePath)
                
                // Save to repository
                cloneRepository.addClone(updatedClone)
                
                // Trigger installation
                installer.installClone(appInfo, updatedClone)
                
                // Update created clone ID
                _createdCloneId.value = updatedClone.id
            } catch (e: Exception) {
                _error.value = "Error creating clone: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear the error message
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Reset the created clone ID
     */
    fun resetCreatedCloneId() {
        _createdCloneId.value = null
    }
    
    /**
     * Get app info for a package name
     */
    private suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            
            return@withContext AppInfo(
                packageName = packageName,
                appName = applicationInfo.loadLabel(packageManager).toString(),
                versionName = packageInfo.versionName ?: "",
                versionCode = packageInfo.longVersionCode,
                isSystemApp = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0,
                appIcon = applicationInfo.loadIcon(packageManager)
            )
        } catch (e: PackageManager.NameNotFoundException) {
            return@withContext null
        }
    }
}