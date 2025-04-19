package com.multiclone.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the clone details screen
 */
@HiltViewModel
class CloneDetailsViewModel @Inject constructor(
    private val cloneRepository: CloneRepository
) : ViewModel() {
    
    // Selected clone ID
    private val _selectedCloneId = MutableStateFlow<String?>(null)
    
    // Current clone being displayed
    private val _currentClone = MutableStateFlow<CloneInfo?>(null)
    val currentClone: StateFlow<CloneInfo?> = _currentClone.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error message
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Initialize with clones from repository
    val clones: Flow<List<CloneInfo>> = cloneRepository.clones
    
    /**
     * Set the ID of the clone to view
     */
    fun setCloneId(cloneId: String) {
        _selectedCloneId.value = cloneId
        loadCloneDetails(cloneId)
    }
    
    /**
     * Load details for a specific clone
     */
    private fun loadCloneDetails(cloneId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Find the clone in the repository
                cloneRepository.initialize() // Make sure repository is initialized
                clones.collect { allClones ->
                    val clone = allClones.find { it.id == cloneId }
                    _currentClone.value = clone
                    _isLoading.value = false
                    
                    if (clone == null) {
                        _error.value = "Clone not found"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error loading clone: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete the current clone
     */
    fun deleteClone() {
        val cloneId = _selectedCloneId.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                cloneRepository.deleteClone(cloneId)
                _currentClone.value = null
            } catch (e: Exception) {
                _error.value = "Error deleting clone: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update notification settings for the current clone
     */
    fun updateNotificationSettings(enabled: Boolean) {
        val cloneId = _selectedCloneId.value ?: return
        
        viewModelScope.launch {
            try {
                cloneRepository.updateNotificationSettings(cloneId, enabled)
                
                // Update current clone in memory
                _currentClone.value = _currentClone.value?.copy(notificationsEnabled = enabled)
            } catch (e: Exception) {
                _error.value = "Error updating notification settings: ${e.message}"
            }
        }
    }
    
    /**
     * Clear the error message
     */
    fun clearError() {
        _error.value = null
    }
}