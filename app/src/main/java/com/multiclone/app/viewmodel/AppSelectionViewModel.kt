package com.multiclone.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadApps()
    }
    
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                _apps.value = getInstalledAppsUseCase()
            } catch (e: Exception) {
                // Handle error - in a real app, we would use an error state
                _apps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchApps(query: String) {
        _searchQuery.value = query
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                _apps.value = if (query.isBlank()) {
                    getInstalledAppsUseCase()
                } else {
                    getInstalledAppsUseCase.search(query)
                }
            } catch (e: Exception) {
                // Handle error
                _apps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}