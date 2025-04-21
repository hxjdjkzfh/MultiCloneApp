package com.multiclone.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.domain.models.InstalledApp
import com.multiclone.app.domain.usecase.CreateCloneUseCase
import com.multiclone.app.domain.usecase.GetAppDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * UI state for the clone setup screen
 */
data class CloneSetupState(
    val packageName: String = "",
    val appDetails: InstalledApp? = null,
    val cloneName: String = "",
    val storageIsolated: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isCreatingClone: Boolean = false,
    val error: String? = null,
    val errorMessage: String? = null
) {
    /**
     * Determines if the clone can be created
     */
    val canCreateClone: Boolean
        get() = appDetails != null && 
                appDetails.canBeCloned &&
                packageName.isNotBlank()
}

/**
 * ViewModel for the clone setup screen
 */
@HiltViewModel
class CloneSetupViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAppDetailsUseCase: GetAppDetailsUseCase,
    private val createCloneUseCase: CreateCloneUseCase
) : ViewModel() {

    // UI state exposed to the composable
    private val _uiState = MutableStateFlow(CloneSetupState(isLoading = true))
    val uiState: StateFlow<CloneSetupState> = _uiState.asStateFlow()
    
    init {
        // Extract package name from saved state if available
        savedStateHandle.get<String>("packageName")?.let { packageName ->
            if (packageName.isNotBlank()) {
                _uiState.update { it.copy(packageName = packageName) }
                loadAppDetails(packageName)
            }
        }
    }
    
    /**
     * Load details for the app to be cloned
     */
    fun loadAppDetails(packageName: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    packageName = packageName,
                    isLoading = true,
                    error = null
                )
            }
            
            try {
                val appDetails = getAppDetailsUseCase(packageName)
                _uiState.update { 
                    it.copy(
                        appDetails = appDetails,
                        isLoading = false,
                        // Set default clone name based on app name
                        cloneName = if (it.cloneName.isBlank()) 
                                        "${appDetails.appName} Clone" 
                                    else 
                                        it.cloneName
                    )
                }
                Timber.d("Loaded app details for $packageName")
            } catch (e: Exception) {
                Timber.e(e, "Error loading app details for $packageName")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Could not load app details: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Update the name for the clone
     */
    fun updateCloneName(name: String) {
        _uiState.update { it.copy(cloneName = name) }
    }
    
    /**
     * Toggle storage isolation setting
     */
    fun toggleStorageIsolation() {
        _uiState.update { it.copy(storageIsolated = !it.storageIsolated) }
    }
    
    /**
     * Toggle notifications setting
     */
    fun toggleNotifications() {
        _uiState.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
    }
    
    /**
     * Create the clone with current settings
     * @return True if clone creation was successful
     */
    suspend fun createClone(): Boolean {
        // Ensure we have all required info
        val currentState = _uiState.value
        val appDetails = currentState.appDetails
        
        if (appDetails == null || !currentState.canCreateClone) {
            _uiState.update { 
                it.copy(errorMessage = "Missing required information to create clone")
            }
            return false
        }
        
        _uiState.update { it.copy(isCreatingClone = true) }
        
        try {
            // Generate a unique ID for the clone
            val cloneId = UUID.randomUUID().toString()
            
            val success = createCloneUseCase(
                packageName = currentState.packageName,
                cloneId = cloneId,
                cloneName = currentState.cloneName,
                storageIsolated = currentState.storageIsolated,
                notificationsEnabled = currentState.notificationsEnabled
            )
            
            if (success) {
                Timber.d("Successfully created clone for ${currentState.packageName}")
                return true
            } else {
                _uiState.update { 
                    it.copy(
                        isCreatingClone = false,
                        errorMessage = "Failed to create clone"
                    )
                }
                return false
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating clone for ${currentState.packageName}")
            _uiState.update { 
                it.copy(
                    isCreatingClone = false,
                    errorMessage = "Error creating clone: ${e.message}"
                )
            }
            return false
        }
    }
    
    /**
     * Clear the error message after it has been shown
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}