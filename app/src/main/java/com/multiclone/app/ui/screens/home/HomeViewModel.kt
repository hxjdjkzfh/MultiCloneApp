package com.multiclone.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * View state for the home screen
 */
data class HomeViewState(
    val clones: List<CloneInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val runningCloneCount: Int = 0
)

/**
 * ViewModel for the home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val cloneManagerService: CloneManagerService
) : ViewModel() {
    
    // UI state
    private val _uiState = MutableStateFlow(HomeViewState(isLoading = true))
    val uiState: StateFlow<HomeViewState> = _uiState.asStateFlow()
    
    init {
        Timber.d("HomeViewModel initialized")
        loadClones()
    }
    
    /**
     * Load all clones from the repository
     */
    fun loadClones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Get the clones from the repository
                val clones = cloneRepository.getAllClones()
                val runningCount = cloneRepository.getRunningCloneCount()
                
                _uiState.update { 
                    it.copy(
                        clones = clones, 
                        runningCloneCount = runningCount,
                        isLoading = false
                    ) 
                }
                
                Timber.d("Loaded ${clones.size} clones, $runningCount running")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load clones")
                _uiState.update { 
                    it.copy(
                        error = "Failed to load cloned apps: ${e.message}",
                        isLoading = false
                    ) 
                }
            }
        }
    }
    
    /**
     * Launch a cloned app
     */
    fun launchClone(cloneId: String) {
        viewModelScope.launch {
            Timber.d("Launching clone: $cloneId")
            
            try {
                if (cloneManagerService.launchApp(cloneId)) {
                    // Update the UI state with the new running count
                    loadClones()
                } else {
                    _uiState.update { 
                        it.copy(error = "Failed to launch the cloned app") 
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to launch clone: $cloneId")
                _uiState.update { 
                    it.copy(error = "Failed to launch the cloned app: ${e.message}") 
                }
            }
        }
    }
    
    /**
     * Delete a cloned app
     */
    fun deleteClone(cloneId: String) {
        viewModelScope.launch {
            Timber.d("Deleting clone: $cloneId")
            
            try {
                if (cloneManagerService.deleteClone(cloneId)) {
                    // Reload the clones after deletion
                    loadClones()
                } else {
                    _uiState.update { 
                        it.copy(error = "Failed to delete the cloned app") 
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete clone: $cloneId")
                _uiState.update { 
                    it.copy(error = "Failed to delete the cloned app: ${e.message}") 
                }
            }
        }
    }
    
    /**
     * Clear any error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        Timber.d("HomeViewModel cleared")
    }
}