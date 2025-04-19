package com.multiclone.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cloneRepository: CloneRepository
) : ViewModel() {
    
    // State for clones
    val clones: Flow<List<CloneInfo>> = cloneRepository.getClones()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Initialize the viewmodel
     */
    init {
        Timber.d("HomeViewModel initialized")
    }
    
    /**
     * Launch a clone
     */
    fun launchClone(cloneId: String) {
        Timber.d("Launching clone: $cloneId")
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Get the clone info
                val cloneInfo = cloneRepository.getCloneById(cloneId)
                
                if (cloneInfo == null) {
                    _error.value = "Clone not found"
                    return@launch
                }
                
                // Update launch statistics
                cloneRepository.updateLaunchStats(cloneId)
                
                // TODO: In a real implementation, we would bind to the CloneManagerService
                // and call launchApp method through the service interface
                // For now, just update the running status
                cloneRepository.updateCloneRunningStatus(cloneId, true)
                
            } catch (e: Exception) {
                Timber.e(e, "Failed to launch clone")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete a clone
     */
    fun deleteClone(cloneId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                Timber.d("Deleting clone: $cloneId")
                val success = cloneRepository.deleteClone(cloneId)
                
                if (!success) {
                    _error.value = "Failed to delete clone"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete clone")
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
}