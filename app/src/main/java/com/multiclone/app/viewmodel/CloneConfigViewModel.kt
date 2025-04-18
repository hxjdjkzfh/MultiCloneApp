package com.multiclone.app.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.domain.usecase.CreateCloneUseCase
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
    private val createCloneUseCase: CreateCloneUseCase
) : ViewModel() {
    
    private val _appInfo = MutableStateFlow<AppInfo?>(null)
    val appInfo: StateFlow<AppInfo?> = _appInfo.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()
    
    /**
     * Load app information for the configuration screen
     */
    fun loadAppInfo(packageName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                val appInfo = appRepository.getAppInfo(packageName)
                if (appInfo != null) {
                    _appInfo.value = appInfo
                } else {
                    _errorMessage.value = "App not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading app info: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Create a new clone with the given configuration
     */
    fun createClone(
        packageName: String,
        customName: String? = null,
        customIcon: Bitmap? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                val result = createCloneUseCase(
                    packageName = packageName,
                    customName = customName,
                    customIcon = customIcon
                )
                
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Failed to create clone: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error creating clone: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}