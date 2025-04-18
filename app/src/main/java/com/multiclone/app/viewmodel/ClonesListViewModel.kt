package com.multiclone.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClonesListViewModel @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val launchCloneUseCase: LaunchCloneUseCase
) : ViewModel() {
    
    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val clones: StateFlow<List<CloneInfo>> = _clones.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()
    
    init {
        // Observe clones from repository
        viewModelScope.launch {
            cloneRepository.clones.collectLatest { clonesList ->
                _clones.value = clonesList
            }
        }
    }
    
    fun loadClones() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                cloneRepository.loadClones()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load clones"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun launchClone(cloneInfo: CloneInfo, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                launchCloneUseCase(cloneInfo).fold(
                    onSuccess = { /* Clone launched successfully */ },
                    onFailure = { throwable ->
                        _errorMessage.value = throwable.message ?: "Failed to launch clone"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteClone(cloneInfo: CloneInfo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isDeleted = cloneRepository.deleteClone(cloneInfo)
                if (!isDeleted) {
                    _errorMessage.value = "Failed to delete clone"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = ""
    }
}
