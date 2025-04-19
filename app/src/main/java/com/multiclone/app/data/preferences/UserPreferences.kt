package com.multiclone.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for Context to create a singleton DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository for managing user preferences
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Get the DataStore instance
    private val dataStore = context.dataStore
    
    // Preference keys
    companion object {
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val LAST_USED_BADGE_COLOR = stringPreferencesKey("last_used_badge_color")
    }
    
    /**
     * Dark theme preference
     */
    val darkThemeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_ENABLED] ?: false
    }
    
    /**
     * Last used badge color preference
     */
    val lastUsedBadgeColor: Flow<String> = dataStore.data.map { preferences ->
        preferences[LAST_USED_BADGE_COLOR] ?: "#57DC54" // Default green
    }
    
    /**
     * Sets the dark theme preference
     */
    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_ENABLED] = enabled
        }
    }
    
    /**
     * Sets the last used badge color
     */
    suspend fun setLastUsedBadgeColor(colorHex: String) {
        dataStore.edit { preferences ->
            preferences[LAST_USED_BADGE_COLOR] = colorHex
        }
    }
    
    /**
     * Resets all preferences to their default values
     */
    suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}