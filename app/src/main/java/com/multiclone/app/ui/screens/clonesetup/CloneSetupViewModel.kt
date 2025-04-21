package com.multiclone.app.ui.screens.clonesetup

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.domain.models.AppInfo
import com.multiclone.app.domain.models.CloneConfig
import com.multiclone.app.domain.models.IsolationLevel
import com.multiclone.app.virtualization.VirtualAppEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI states for the Clone Setup screen
 */
sealed class CloneSetupUiState {
    object Loading : CloneSetupUiState()
    data class AppDetailsLoaded(val appInfo: AppInfo) : CloneSetupUiState()
    object Creating : CloneSetupUiState()
    data class Error(val message: String) : CloneSetupUiState()
}

/**
 * Events emitted by the Clone Setup ViewModel
 */
sealed class CloneSetupEvent {
    data class Error(val message: String) : CloneSetupEvent()
    object CloneCreated : CloneSetupEvent()
}

/**
 * ViewModel for the clone setup screen.
 * Handles app details loading and clone creation.
 */
@HiltViewModel
class CloneSetupViewModel @Inject constructor(
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<CloneSetupUiState>(CloneSetupUiState.Loading)
    val uiState: StateFlow<CloneSetupUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<CloneSetupEvent>()
    val events: SharedFlow<CloneSetupEvent> = _events.asSharedFlow()
    
    // Currently selected package name
    private var packageName: String? = null
    
    // Custom icon for the clone
    private var customIcon: Bitmap? = null
    
    /**
     * Sets the package name and loads app details
     */
    fun setPackageName(name: String) {
        if (packageName == name) return
        
        packageName = name
        loadAppDetails(name)
    }
    
    /**
     * Loads details about the app to be cloned
     */
    private fun loadAppDetails(packageName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = CloneSetupUiState.Loading
                
                // Initialize the virtual app engine
                virtualAppEngine.initialize()
                
                // Get installed apps
                val apps = virtualAppEngine.getCloneableApps()
                
                // Find the selected app
                val appInfo = apps.find { it.packageName == packageName }
                
                if (appInfo != null) {
                    _uiState.value = CloneSetupUiState.AppDetailsLoaded(appInfo)
                } else {
                    _uiState.value = CloneSetupUiState.Error("App not found")
                    _events.emit(CloneSetupEvent.Error("Could not find the selected app"))
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Error loading app details for $packageName")
                _uiState.value = CloneSetupUiState.Error(
                    e.message ?: "Failed to load app details"
                )
                _events.emit(CloneSetupEvent.Error(
                    e.message ?: "Failed to load app details"
                ))
            }
        }
    }
    
    /**
     * Updates the custom icon for the clone
     */
    fun setCustomIcon(icon: Bitmap?) {
        customIcon = icon
    }
    
    /**
     * Creates a new clone with the specified configuration
     */
    fun createClone(
        customName: String? = null,
        isolationLevel: IsolationLevel = IsolationLevel.STANDARD
    ) {
        viewModelScope.launch {
            try {
                val package_name = packageName
                    ?: throw IllegalStateException("Package name not set")
                
                _uiState.value = CloneSetupUiState.Creating
                
                // Create the clone config
                val config = CloneConfig(
                    customName = if (customName.isNullOrBlank()) null else customName,
                    customIcon = customIcon,
                    isolationLevel = isolationLevel
                )
                
                // Create the clone
                val result = virtualAppEngine.createClone(package_name, config)
                
                // Handle result
                if (result.isSuccess) {
                    _events.emit(CloneSetupEvent.CloneCreated)
                } else {
                    val error = result.exceptionOrNull()
                    _events.emit(CloneSetupEvent.Error(
                        error?.message ?: "Unknown error creating clone"
                    ))
                    _uiState.value = CloneSetupUiState.Error(
                        error?.message ?: "Unknown error creating clone"
                    )
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Error creating clone")
                _uiState.value = CloneSetupUiState.Error(
                    e.message ?: "Failed to create clone"
                )
                _events.emit(CloneSetupEvent.Error(
                    e.message ?: "Failed to create clone"
                ))
            }
        }
    }
}