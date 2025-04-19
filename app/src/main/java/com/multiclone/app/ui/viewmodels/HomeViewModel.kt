package com.multiclone.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cloneRepository: CloneRepository
) : ViewModel() {
    
    // Flow of clones from repository
    val clones: Flow<List<CloneInfo>> = cloneRepository.clones
    
    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        // Initialize the repository and load clones
        viewModelScope.launch {
            try {
                _isLoading.value = true
                cloneRepository.initialize()
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
            cloneRepository.updateNotificationSettings(cloneId, enabled)
        }
    }
}