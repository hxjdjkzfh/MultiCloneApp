package com.multiclone.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the HomeScreen (ClonesListScreen)
 */
@HiltViewModel
class ClonesListViewModel @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val launchCloneUseCase: LaunchCloneUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClonesListUiState())
    val uiState: StateFlow<ClonesListUiState> = _uiState.asStateFlow()

    init {
        loadClones()
    }

    /**
     * Loads the list of clones
     */
    fun loadClones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                cloneRepository.getAllClones().collect { clones ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            clones = clones.sortedByDescending { clone -> clone.lastUsedTime },
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading clones", e)
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
     * Launches a cloned app
     */
    fun launchClone(cloneId: String) {
        viewModelScope.launch {
            try {
                val result = launchCloneUseCase(cloneId)
                
                if (result.isFailure) {
                    val exception = result.exceptionOrNull()
                    Log.e(TAG, "Error launching clone", exception)
                    _uiState.update { 
                        it.copy(error = exception?.message ?: "Failed to launch clone")
                    }
                }
                
                // Refresh the list to update last used time
                loadClones()
            } catch (e: Exception) {
                Log.e(TAG, "Error launching clone", e)
                _uiState.update { 
                    it.copy(error = e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    /**
     * Deletes a cloned app
     */
    fun deleteClone(cloneId: String) {
        viewModelScope.launch {
            try {
                val success = cloneRepository.deleteClone(cloneId)
                
                if (!success) {
                    _uiState.update { 
                        it.copy(error = "Failed to delete clone")
                    }
                    return@launch
                }
                
                // Refresh the list
                loadClones()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting clone", e)
                _uiState.update { 
                    it.copy(error = e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    companion object {
        private const val TAG = "ClonesListViewModel"
    }
}

/**
 * UI state for the ClonesListScreen
 */
data class ClonesListUiState(
    val isLoading: Boolean = false,
    val clones: List<CloneInfo> = emptyList(),
    val error: String? = null
)