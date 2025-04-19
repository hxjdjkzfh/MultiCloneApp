package com.multiclone.app

import androidx.lifecycle.ViewModel
import com.multiclone.app.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel for MainActivity that provides theming preferences
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    /**
     * Observable dark theme preference
     */
    val darkThemeEnabled: Flow<Boolean> = userPreferences.darkThemeEnabled
}