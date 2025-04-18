package com.multiclone.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.domain.usecase.CreateCloneUseCase
import com.multiclone.app.domain.usecase.CreateShortcutUseCase
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the CloneConfigScreen
 */
@HiltViewModel
class CloneConfigViewModel @Inject constructor(
    private val createCloneUseCase: CreateCloneUseCase,
    private val createShortcutUseCase: CreateShortcutUseCase,
    private val launchCloneUseCase: LaunchCloneUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CloneConfigUiState())
    val uiState: StateFlow<CloneConfigUiState> = _uiState.asStateFlow()

    /**
     * Sets the selected app to clone
     */
    fun setSelectedApp(appInfo: AppInfo) {
        _uiState.update { it.copy(selectedApp = appInfo) }
    }

    /**
     * Sets whether to create a shortcut for the clone
     */
    fun setCreateShortcut(createShortcut: Boolean) {
        _uiState.update { it.copy(createShortcut = createShortcut) }
    }

    /**
     * Sets whether to launch the clone after creation
     */
    fun setLaunchAfterCreation(launchAfterCreation: Boolean) {
        _uiState.update { it.copy(launchAfterCreation = launchAfterCreation) }
    }

    /**
     * Creates a clone of the selected app
     */
    fun createClone(cloneName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val selectedApp = uiState.value.selectedApp
            if (selectedApp == null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "No app selected"
                    )
                }
                return@launch
            }
            
            try {
                // Create the clone
                val cloneResult = createCloneUseCase(
                    packageName = selectedApp.packageName,
                    cloneName = cloneName
                )
                
                if (cloneResult.isFailure) {
                    val exception = cloneResult.exceptionOrNull()
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception?.message ?: "Failed to create clone"
                        )
                    }
                    return@launch
                }
                
                val cloneId = cloneResult.getOrNull()
                if (cloneId == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to create clone: No clone ID returned"
                        )
                    }
                    return@launch
                }
                
                // Create shortcut if needed
                if (uiState.value.createShortcut) {
                    val shortcutResult = createShortcutUseCase(
                        cloneId = cloneId,
                        cloneName = cloneName
                    )
                    
                    if (shortcutResult.isFailure) {
                        // Just log the error, don't fail the whole operation
                        val exception = shortcutResult.exceptionOrNull()
                        println("Failed to create shortcut: ${exception?.message}")
                    }
                }
                
                // Launch the clone if needed
                if (uiState.value.launchAfterCreation) {
                    val launchResult = launchCloneUseCase(cloneId)
                    
                    if (launchResult.isFailure) {
                        // Just log the error, don't fail the whole operation
                        val exception = launchResult.exceptionOrNull()
                        println("Failed to launch clone: ${exception?.message}")
                    }
                }
                
                // Update UI state with success
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        cloneId = cloneId,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    /**
     * Resets the error state
     */
    fun resetErrorState() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI state for the CloneConfigScreen
 */
data class CloneConfigUiState(
    val isLoading: Boolean = false,
    val selectedApp: AppInfo? = null,
    val createShortcut: Boolean = true,
    val launchAfterCreation: Boolean = false,
    val cloneId: String? = null,
    val error: String? = null
)