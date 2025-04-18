package com.multiclone.app.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.usecase.CreateCloneUseCase
import com.multiclone.app.domain.usecase.CreateShortcutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the clone configuration screen
 */
@HiltViewModel
class CloneConfigViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val cloneRepository: CloneRepository,
    private val createCloneUseCase: CreateCloneUseCase,
    private val createShortcutUseCase: CreateShortcutUseCase
) : ViewModel() {
    
    // UI state for the clone configuration screen
    private val _uiState = MutableStateFlow(CloneConfigUiState())
    val uiState: StateFlow<CloneConfigUiState> = _uiState.asStateFlow()
    
    /**
     * Initialize the UI state with the selected package
     */
    fun initialize(packageName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val appInfo = appRepository.getAppInfo(packageName)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        originalAppName = appInfo.appName,
                        originalAppIcon = appInfo.icon
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load app information"
                    )
                }
            }
        }
    }
    
    /**
     * Create a new clone of the app
     */
    fun createClone(
        packageName: String,
        cloneName: String,
        customIconColor: Color,
        createShortcut: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingClone = true) }
            
            try {
                // Create the clone
                val cloneInfo = createCloneUseCase.execute(
                    packageName = packageName,
                    cloneName = cloneName,
                    customIconColorHex = String.format("#%06X", 0xFFFFFF and customIconColor.hashCode())
                )
                
                // Create a shortcut if requested
                if (createShortcut) {
                    createShortcutUseCase.execute(cloneInfo)
                    
                    // Update shortcut status
                    val updatedClone = cloneInfo.copy(hasShortcut = true)
                    cloneRepository.updateClone(updatedClone)
                }
                
                // Update UI state
                _uiState.update { 
                    it.copy(
                        isCreatingClone = false,
                        cloneCreated = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isCreatingClone = false,
                        error = e.message ?: "Failed to create clone"
                    )
                }
            }
        }
    }
}

/**
 * UI state for clone configuration screen
 */
data class CloneConfigUiState(
    val isLoading: Boolean = false,
    val originalAppName: String = "",
    val originalAppIcon: Bitmap? = null,
    val isCreatingClone: Boolean = false,
    val cloneCreated: Boolean = false,
    val error: String? = null
)