package com.multiclone.app.ui.screens.cloneconfig

import android.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the CloneConfig screen
 */
@HiltViewModel
class CloneConfigViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {
    
    // The selected app package name
    private val packageName = savedStateHandle.get<String>("packageName") ?: ""
    
    // The clone ID when editing
    private val cloneId = savedStateHandle.get<String>("cloneId")
    
    // App info
    private val _appInfo = MutableStateFlow<AppInfo?>(null)
    val appInfo: StateFlow<AppInfo?> = _appInfo.asStateFlow()
    
    // Clone name
    private val _cloneName = MutableStateFlow("")
    val cloneName: StateFlow<String> = _cloneName.asStateFlow()
    
    // Custom color
    private val _customColor = MutableStateFlow<Int?>(null)
    val customColor: StateFlow<Int?> = _customColor.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Advanced settings
    private val _useCustomIsolation = MutableStateFlow(false)
    val useCustomIsolation: StateFlow<Boolean> = _useCustomIsolation.asStateFlow()
    
    private val _isolateStorage = MutableStateFlow(true)
    val isolateStorage: StateFlow<Boolean> = _isolateStorage.asStateFlow()
    
    private val _isolateAccounts = MutableStateFlow(true)
    val isolateAccounts: StateFlow<Boolean> = _isolateAccounts.asStateFlow()
    
    private val _isolateLocation = MutableStateFlow(false)
    val isolateLocation: StateFlow<Boolean> = _isolateLocation.asStateFlow()
    
    private val _useCustomLauncher = MutableStateFlow(false)
    val useCustomLauncher: StateFlow<Boolean> = _useCustomLauncher.asStateFlow()
    
    // Save success
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()
    
    // Is this an edit operation
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()
    
    /**
     * Initialize the viewmodel
     */
    init {
        Timber.d("CloneConfigViewModel initialized with packageName: $packageName, cloneId: $cloneId")
        
        _isEditMode.value = cloneId != null
        
        if (cloneId != null) {
            // Edit mode
            loadExistingClone(cloneId)
        } else if (packageName.isNotEmpty()) {
            // New clone mode
            loadAppInfo(packageName)
        } else {
            // Invalid state
            _error.value = "Invalid parameters"
            _isLoading.value = false
        }
    }
    
    /**
     * Load app info for a given package name
     */
    private fun loadAppInfo(packageName: String) {
        viewModelScope.launch {
            try {
                val appInfoList = virtualAppEngine.getCloneableApps()
                val foundApp = appInfoList.find { it.packageName == packageName }
                
                if (foundApp != null) {
                    _appInfo.value = foundApp
                    _cloneName.value = foundApp.appName
                    
                    // Default color
                    _customColor.value = Color.parseColor("#FF1976D2")
                } else {
                    _error.value = "App not found"
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load app info")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load existing clone for editing
     */
    private fun loadExistingClone(cloneId: String) {
        viewModelScope.launch {
            try {
                val clone = cloneRepository.getCloneById(cloneId)
                
                if (clone != null) {
                    // Load app info
                    val appInfoList = virtualAppEngine.getCloneableApps()
                    val foundApp = appInfoList.find { it.packageName == clone.originalPackageName }
                    
                    if (foundApp != null) {
                        _appInfo.value = foundApp
                        
                        // Apply clone settings
                        _cloneName.value = clone.customName ?: foundApp.appName
                        _customColor.value = clone.customColor
                        
                        // Advanced settings
                        _useCustomIsolation.value = clone.useCustomIsolation
                        _isolateStorage.value = clone.isolateStorage
                        _isolateAccounts.value = clone.isolateAccounts
                        _isolateLocation.value = clone.isolateLocation
                        _useCustomLauncher.value = clone.useCustomLauncher
                    } else {
                        _error.value = "Original app not found"
                    }
                } else {
                    _error.value = "Clone not found"
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load clone")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update clone name
     */
    fun setCloneName(name: String) {
        _cloneName.value = name
    }
    
    /**
     * Update custom color
     */
    fun setCustomColor(color: Int) {
        _customColor.value = color
    }
    
    /**
     * Toggle custom isolation settings
     */
    fun toggleCustomIsolation(enabled: Boolean) {
        _useCustomIsolation.value = enabled
    }
    
    /**
     * Toggle storage isolation
     */
    fun toggleIsolateStorage(enabled: Boolean) {
        _isolateStorage.value = enabled
    }
    
    /**
     * Toggle account isolation
     */
    fun toggleIsolateAccounts(enabled: Boolean) {
        _isolateAccounts.value = enabled
    }
    
    /**
     * Toggle location isolation
     */
    fun toggleIsolateLocation(enabled: Boolean) {
        _isolateLocation.value = enabled
    }
    
    /**
     * Toggle custom launcher
     */
    fun toggleCustomLauncher(enabled: Boolean) {
        _useCustomLauncher.value = enabled
    }
    
    /**
     * Save the clone
     */
    fun saveClone() {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val app = _appInfo.value
                
                if (app == null) {
                    _error.value = "App not found"
                    return@launch
                }
                
                val clone = CloneInfo(
                    id = cloneId ?: UUID.randomUUID().toString(),
                    originalPackageName = app.packageName,
                    customName = _cloneName.value.takeIf { it != app.appName },
                    customColor = _customColor.value,
                    useCustomIsolation = _useCustomIsolation.value,
                    isolateStorage = _isolateStorage.value,
                    isolateAccounts = _isolateAccounts.value,
                    isolateLocation = _isolateLocation.value,
                    useCustomLauncher = _useCustomLauncher.value,
                    isRunning = false,
                    lastLaunchTime = null,
                    launchCount = 0
                )
                
                if (_isEditMode.value) {
                    val success = cloneRepository.updateClone(clone)
                    if (!success) {
                        _error.value = "Failed to update clone"
                        return@launch
                    }
                } else {
                    val success = cloneRepository.createClone(clone)
                    
                    if (!success) {
                        _error.value = "Failed to create clone"
                        return@launch
                    }
                    
                    // In a real implementation, we'd now create the actual app clone
                    // through the VirtualAppEngine
                }
                
                _saveSuccess.value = true
                
            } catch (e: Exception) {
                Timber.e(e, "Failed to save clone")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Reset save success state
     */
    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}