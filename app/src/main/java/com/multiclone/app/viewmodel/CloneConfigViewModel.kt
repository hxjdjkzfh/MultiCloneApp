package com.multiclone.app.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.domain.usecase.CreateCloneUseCase
import com.multiclone.app.domain.usecase.CreateShortcutUseCase
import com.multiclone.app.utils.IconUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CloneConfigViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val createCloneUseCase: CreateCloneUseCase,
    private val createShortcutUseCase: CreateShortcutUseCase
) : ViewModel() {
    private val _appInfo = MutableStateFlow<AppInfo?>(null)
    val appInfo: StateFlow<AppInfo?> = _appInfo.asStateFlow()
    
    private val _cloneName = MutableStateFlow("")
    val cloneName: StateFlow<String> = _cloneName.asStateFlow()
    
    private val _customIcon = MutableStateFlow<Bitmap?>(null)
    val customIcon: StateFlow<Bitmap?> = _customIcon.asStateFlow()
    
    private val _isCreatingClone = MutableStateFlow(false)
    val isCreatingClone: StateFlow<Boolean> = _isCreatingClone.asStateFlow()
    
    private val _isCloneCreated = MutableStateFlow(false)
    val isCloneCreated: StateFlow<Boolean> = _isCloneCreated.asStateFlow()
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()
    
    fun loadAppInfo(packageName: String) {
        viewModelScope.launch {
            try {
                val info = appRepository.getAppInfo(packageName)
                _appInfo.value = info
                
                if (info != null) {
                    // Set default clone name
                    _cloneName.value = "Clone - ${info.name}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load app info"
            }
        }
    }
    
    fun onCloneNameChanged(name: String) {
        _cloneName.value = name
    }
    
    fun onCustomIconSelected(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val bitmap = IconUtils.loadBitmapFromUri(uri, context)
                _customIcon.value = bitmap
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load icon: ${e.message}"
            }
        }
    }
    
    fun createClone(context: Context) {
        val app = _appInfo.value ?: return
        val name = _cloneName.value
        
        // Validate input
        val validationError = createCloneUseCase.validateCloneParams(app.packageName, name)
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }
        
        viewModelScope.launch {
            _isCreatingClone.value = true
            
            try {
                createCloneUseCase(
                    packageName = app.packageName,
                    cloneName = name,
                    customIcon = _customIcon.value
                ).fold(
                    onSuccess = { cloneInfo ->
                        // Create shortcut
                        createShortcutForClone(cloneInfo, context)
                        _isCloneCreated.value = true
                    },
                    onFailure = { throwable ->
                        _errorMessage.value = throwable.message ?: "Failed to create clone"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isCreatingClone.value = false
            }
        }
    }
    
    private suspend fun createShortcutForClone(cloneInfo: CloneInfo, context: Context) {
        createShortcutUseCase(cloneInfo, context).fold(
            onSuccess = { /* Shortcut created successfully */ },
            onFailure = { throwable ->
                // We still consider the clone creation successful even if shortcut creation fails
                _errorMessage.value = "Clone created successfully, but shortcut creation failed: ${throwable.message}"
            }
        )
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = ""
    }
}
