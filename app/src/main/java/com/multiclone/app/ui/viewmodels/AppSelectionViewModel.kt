package com.multiclone.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.ClonedAppInstaller
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val clonedAppInstaller: ClonedAppInstaller,
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {
    
    // List of installed apps
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Last created clone
    private val _lastCreatedClone = MutableStateFlow<CloneInfo?>(null)
    val lastCreatedClone: StateFlow<CloneInfo?> = _lastCreatedClone.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadInstalledApps()
    }
    
    /**
     * Load all installed apps that can be cloned
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val apps = clonedAppInstaller.getInstalledApps()
                _installedApps.value = apps
            } catch (e: Exception) {
                _error.value = "Failed to load apps: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Creates a clone of the selected app
     */
    fun createClone(packageName: String, displayName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                if (!virtualAppEngine.canClonePackage(packageName)) {
                    _error.value = "This app cannot be cloned"
                    return@launch
                }
                
                // Create the clone
                val clone = cloneRepository.createClone(packageName, displayName)
                
                if (clone != null) {
                    _lastCreatedClone.value = clone
                } else {
                    _error.value = "Failed to create clone"
                }
            } catch (e: Exception) {
                _error.value = "Error creating clone: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Check if a package can be cloned
     */
    fun canClonePackage(packageName: String): Boolean {
        return virtualAppEngine.canClonePackage(packageName)
    }
    
    /**
     * Clear the last created clone
     */
    fun clearLastCreatedClone() {
        _lastCreatedClone.value = null
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }
}