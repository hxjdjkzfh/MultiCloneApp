package com.multiclone.app.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.domain.usecase.CreateCloneUseCase
import com.multiclone.app.utils.IconUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the clone configuration screen
 */
@HiltViewModel
class CloneConfigViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val createCloneUseCase: CreateCloneUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get packageName from navigation arguments
    private val packageName: String = checkNotNull(savedStateHandle["packageName"])
    
    private val _selectedApp = MutableStateFlow<AppInfo?>(null)
    val selectedApp: StateFlow<AppInfo?> = _selectedApp.asStateFlow()
    
    private val _displayName = MutableStateFlow<String>("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()
    
    private val _customIcon = MutableStateFlow<Bitmap?>(null)
    val customIcon: StateFlow<Bitmap?> = _customIcon.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _cloneCreated = MutableStateFlow<CloneInfo?>(null)
    val cloneCreated: StateFlow<CloneInfo?> = _cloneCreated.asStateFlow()

    init {
        loadAppInfo()
    }
    
    private fun loadAppInfo() {
        val app = appRepository.getAppInfo(packageName)
        _selectedApp.value = app
        _displayName.value = app?.appName ?: ""
    }
    
    fun updateDisplayName(name: String) {
        _displayName.value = name
    }
    
    fun updateCustomIcon(icon: Bitmap?) {
        _customIcon.value = icon
    }
    
    fun createClone() {
        val app = _selectedApp.value ?: return
        val name = _displayName.value.ifEmpty { app.appName }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                createCloneUseCase(
                    appInfo = app,
                    displayName = name,
                    customIcon = _customIcon.value
                ).onSuccess { clone ->
                    _cloneCreated.value = clone
                }.onFailure {
                    // Handle error - in a real app, we would use an error state
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetCloneCreated() {
        _cloneCreated.value = null
    }
}