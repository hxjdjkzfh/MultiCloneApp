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
 * ViewModel for home screen with list of clones
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cloneRepository: CloneRepository
) : ViewModel() {
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error message
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Initialize with clones from repository
    val clones: Flow<List<CloneInfo>> = cloneRepository.clones
    
    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                cloneRepository.initialize()
            } catch (e: Exception) {
                _error.value = "Error loading clones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete a clone by ID
     */
    fun deleteClone(cloneId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                cloneRepository.deleteClone(cloneId)
            } catch (e: Exception) {
                _error.value = "Error deleting clone: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update notification settings for a clone
     */
    fun updateNotificationSettings(cloneId: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                cloneRepository.updateNotificationSettings(cloneId, enabled)
            } catch (e: Exception) {
                _error.value = "Error updating notification settings: ${e.message}"
            }
        }
    }
    
    /**
     * Update last used time when launching a clone
     */
    fun launchClone(cloneId: String) {
        viewModelScope.launch {
            try {
                cloneRepository.updateLastUsedTime(cloneId)
            } catch (e: Exception) {
                _error.value = "Error updating clone usage time: ${e.message}"
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