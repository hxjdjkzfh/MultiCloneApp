package com.multiclone.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.usecase.CreateShortcutUseCase
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the clones list screen
 */
@HiltViewModel
class ClonesListViewModel @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val launchCloneUseCase: LaunchCloneUseCase,
    private val createShortcutUseCase: CreateShortcutUseCase
) : ViewModel() {
    
    // UI state for the clones list screen
    private val _uiState = MutableStateFlow(ClonesListUiState())
    val uiState: StateFlow<ClonesListUiState> = _uiState.asStateFlow()
    
    init {
        loadClones()
    }
    
    /**
     * Load all cloned apps
     */
    private fun loadClones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                cloneRepository.getAllClones().collectLatest { clones ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            clones = clones
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load clones"
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
            _uiState.update { it.copy(isLaunchingClone = true) }
            
            try {
                val clone = cloneRepository.getCloneById(cloneId)
                clone?.let {
                    launchCloneUseCase.execute(it)
                    
                    // Update last used time
                    val updatedClone = it.copy(lastUsedTime = System.currentTimeMillis())
                    cloneRepository.updateClone(updatedClone)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to launch clone")
                }
            } finally {
                _uiState.update { it.copy(isLaunchingClone = false) }
            }
        }
    }
    
    /**
     * Create a shortcut for a cloned app
     */
    fun createShortcut(cloneId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingShortcut = true) }
            
            try {
                val clone = cloneRepository.getCloneById(cloneId)
                clone?.let {
                    createShortcutUseCase.execute(it)
                    
                    // Update shortcut status
                    val updatedClone = it.copy(hasShortcut = true)
                    cloneRepository.updateClone(updatedClone)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to create shortcut")
                }
            } finally {
                _uiState.update { it.copy(isCreatingShortcut = false) }
            }
        }
    }
    
    /**
     * Delete a cloned app
     */
    fun deleteClone(cloneId: String) {
        viewModelScope.launch {
            try {
                cloneRepository.deleteClone(cloneId)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to delete clone")
                }
            }
        }
    }
}

/**
 * UI state for clones list screen
 */
data class ClonesListUiState(
    val isLoading: Boolean = false,
    val clones: List<CloneInfo> = emptyList(),
    val isLaunchingClone: Boolean = false,
    val isCreatingShortcut: Boolean = false,
    val error: String? = null
)