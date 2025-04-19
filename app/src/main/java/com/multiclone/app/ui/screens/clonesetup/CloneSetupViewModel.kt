package com.multiclone.app.ui.screens.clonesetup

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
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
data class CloneSetupUiState(
    val appInfo: AppInfo? = null,
    val cloneName: String = "",
    val selectedBadgeColor: Color? = Color(0xFF4CAF50), // Default green color
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the clone setup screen
 */
@HiltViewModel
class CloneSetupViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Package name of the app to clone, passed as a navigation argument
    private val packageName: String = savedStateHandle.get<String>("packageName") ?: ""
    
    // Private mutable state flow
    private val _uiState = MutableStateFlow(CloneSetupUiState())
    
    // Public immutable state flow
    val uiState: StateFlow<CloneSetupUiState> = _uiState.asStateFlow()
    
    init {
        // Load app info when ViewModel is created
        loadAppInfo()
    }
    
    /**
     * Loads information about the app to be cloned
     */
    private fun loadAppInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                if (packageName.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Invalid package name"
                        )
                    }
                    return@launch
                }
                
                val appInfo = appRepository.getAppInfo(packageName)
                
                if (appInfo == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "App not found"
                        )
                    }
                    return@launch
                }
                
                // App found, update state
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        appInfo = appInfo
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading app info")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading app info: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Updates the clone name
     */
    fun updateCloneName(name: String) {
        _uiState.update { it.copy(cloneName = name) }
    }
    
    /**
     * Updates the badge color
     */
    fun updateBadgeColor(color: Color) {
        _uiState.update { it.copy(selectedBadgeColor = color) }
    }
    
    /**
     * Toggles notification settings
     */
    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }
    
    /**
     * Creates a clone with the current settings
     * 
     * @param onSuccess Callback with the clone ID when successful
     */
    fun createClone(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val appInfo = currentState.appInfo ?: return@launch
            
            _uiState.update { it.copy(isCreating = true) }
            
            try {
                // Generate unique ID for the clone
                val cloneId = UUID.randomUUID().toString()
                
                // Convert color to hex string
                val colorHex = currentState.selectedBadgeColor?.let { color ->
                    "#" + Integer.toHexString(color.toArgb()).substring(2)
                }
                
                // Create CloneInfo object
                val cloneInfo = CloneInfo(
                    id = cloneId,
                    packageName = appInfo.packageName,
                    originalAppName = appInfo.appName,
                    cloneName = currentState.cloneName.takeIf { it.isNotBlank() },
                    badgeColorHex = colorHex,
                    isNotificationsEnabled = currentState.notificationsEnabled,
                    creationTime = System.currentTimeMillis(),
                    originalAppIcon = appInfo.appIcon
                )
                
                // Install the clone using virtual engine
                val engineSuccess = virtualAppEngine.installClone(appInfo.packageName, cloneInfo)
                
                if (!engineSuccess) {
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            errorMessage = "Failed to create virtual environment for clone"
                        )
                    }
                    return@launch
                }
                
                // Save clone to repository
                val repoSuccess = cloneRepository.addClone(cloneInfo)
                
                if (!repoSuccess) {
                    // Try to clean up the virtual environment since repository save failed
                    virtualAppEngine.removeClone(cloneInfo)
                    
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            errorMessage = "Failed to save clone information"
                        )
                    }
                    return@launch
                }
                
                // All successful, notify success
                _uiState.update { it.copy(isCreating = false) }
                onSuccess(cloneId)
                
            } catch (e: Exception) {
                Timber.e(e, "Error creating clone")
                _uiState.update {
                    it.copy(
                        isCreating = false,
                        errorMessage = "Error creating clone: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Dismisses the current error message
     */
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    companion object {
        /**
         * Creates a preview version of the ViewModel with sample data
         */
        fun createPreview(): CloneSetupViewModel {
            val mockAppRepository = object : AppRepository {
                override suspend fun getInstalledApps(): List<AppInfo> = emptyList()
                
                override suspend fun getAppInfo(packageName: String): AppInfo? {
                    return AppInfo.createSimplified(
                        packageName = "com.example.app",
                        appName = "Example App"
                    )
                }
                
                override suspend fun isAppInstalled(packageName: String): Boolean = true
            }
            
            val mockCloneRepository = object : CloneRepository {
                override val clones = MutableStateFlow<List<CloneInfo>>(emptyList())
                override suspend fun addClone(clone: CloneInfo): Boolean = true
                override suspend fun updateClone(clone: CloneInfo): Boolean = true
                override suspend fun removeClone(cloneId: String): Boolean = true
                override suspend fun getCloneById(cloneId: String): CloneInfo? = null
                override suspend fun loadClones(): Boolean = true
            }
            
            val mockEngine = object : VirtualAppEngine {
                override suspend fun installClone(packageName: String, cloneInfo: CloneInfo): Boolean = true
                override suspend fun launchClone(cloneInfo: CloneInfo): Boolean = true
                override suspend fun removeClone(cloneInfo: CloneInfo): Boolean = true
                override suspend fun isCloneInstalled(cloneInfo: CloneInfo): Boolean = false
                override suspend fun updateCloneSettings(cloneInfo: CloneInfo): Boolean = true
            }
            
            val mockSavedStateHandle = SavedStateHandle().apply {
                set("packageName", "com.example.app")
            }
            
            return CloneSetupViewModel(
                mockAppRepository,
                mockCloneRepository,
                mockEngine,
                mockSavedStateHandle
            ).apply {
                // Set sample data for preview
                viewModelScope.launch {
                    val appInfo = AppInfo.createSimplified(
                        packageName = "com.example.app",
                        appName = "Example App"
                    )
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            appInfo = appInfo,
                            cloneName = "Work Profile",
                            selectedBadgeColor = Color(0xFF2196F3)
                        )
                    }
                }
            }
        }
    }
}