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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the clones list screens
 */
@HiltViewModel
class ClonesListViewModel @Inject constructor(
    private val getClonesUseCase: GetClonesUseCase,
    private val launchCloneUseCase: LaunchCloneUseCase,
    private val deleteCloneUseCase: DeleteCloneUseCase
) : ViewModel() {
    
    private val _allClones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val allClones: StateFlow<List<CloneInfo>> = _allClones.asStateFlow()
    
    private val _recentClones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val recentClones: StateFlow<List<CloneInfo>> = _recentClones.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Load all clones
     */
    fun loadAllClones() {
        viewModelScope.launch {
            _isLoading.value = true
            
            getClonesUseCase().collectLatest { clones ->
                _allClones.value = clones
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load recent clones (limited by count)
     */
    fun loadRecentClones(count: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            
            getClonesUseCase().collectLatest { clones ->
                _recentClones.value = clones
                    .sortedByDescending { it.lastUsedTimestamp }
                    .take(count)
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Launch a clone by its ID
     */
    fun launchClone(cloneId: String) {
        viewModelScope.launch {
            launchCloneUseCase(cloneId)
        }
    }
    
    /**
     * Delete a clone by its ID
     */
    fun deleteClone(cloneId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            deleteCloneUseCase(cloneId)
            
            // Refresh lists after deletion
            loadAllClones()
            loadRecentClones(5)
        }
    }
}