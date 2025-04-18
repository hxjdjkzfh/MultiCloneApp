package com.multiclone.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.domain.usecase.DeleteCloneUseCase
import com.multiclone.app.domain.usecase.GetClonesUseCase
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the clones list screen
 */
@HiltViewModel
class ClonesListViewModel @Inject constructor(
    private val getClonesUseCase: GetClonesUseCase,
    private val launchCloneUseCase: LaunchCloneUseCase,
    private val deleteCloneUseCase: DeleteCloneUseCase
) : ViewModel() {

    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val clones: StateFlow<List<CloneInfo>> = _clones.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadClones()
    }
    
    fun loadClones() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                _clones.value = getClonesUseCase()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load clones: ${e.message}"
                _clones.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun launchClone(cloneId: String) {
        launchCloneUseCase(cloneId)
        loadClones() // Refresh the list to update last used timestamp
    }
    
    fun deleteClone(cloneId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                deleteCloneUseCase(cloneId)
                    .onSuccess {
                        loadClones() // Refresh the list
                    }
                    .onFailure { error ->
                        _errorMessage.value = "Failed to delete clone: ${error.message}"
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete clone: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}