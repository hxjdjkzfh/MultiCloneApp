package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

/**
 * Model class representing a cloned app.
 * Contains all relevant information about a cloned app.
 */
@Serializable
data class CloneInfo(
    // Basic information
    val id: String,
    val packageName: String,
    val originalAppName: String,
    
    // Clone customization
    val cloneName: String? = null,
    val badgeColorHex: String? = null,
    
    // Clone statistics
    val creationTime: Long = System.currentTimeMillis(),
    val lastLaunchTime: Long = 0,
    val launchCount: Int = 0,
    
    // Clone settings
    val isNotificationsEnabled: Boolean = true,
    val customIcon: String? = null, // Path to custom icon file
    
    // Transient properties (not serialized)
    @Transient
    val originalAppIcon: Drawable? = null,
    @Transient
    val customIconDrawable: Drawable? = null
) {
    /**
     * Gets the display name for the clone.
     * If a custom name is set, it returns that, otherwise returns the original app name.
     */
    fun getDisplayName(): String {
        return cloneName ?: "$originalAppName (Clone)"
    }
    
    /**
     * Gets the badge color for the clone.
     * If a custom color is set, it returns that, otherwise returns null.
     */
    val badgeColor: Color?
        get() = badgeColorHex?.let { hex ->
            try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (e: Exception) {
                null
            }
        }
    
    /**
     * Gets the clone directory path.
     * This is where all clone-specific data is stored.
     * 
     * @param baseDir The base directory for all clones
     * @return The directory for this specific clone
     */
    fun getCloneDirectory(baseDir: File): File {
        return File(baseDir, id)
    }
    
    /**
     * Creates a new instance with updated launch statistics
     */
    fun updateLaunchStats(): CloneInfo {
        return this.copy(
            lastLaunchTime = System.currentTimeMillis(),
            launchCount = launchCount + 1
        )
    }
    
    /**
     * Creates a new instance with updated name
     */
    fun updateName(newName: String): CloneInfo {
        return this.copy(
            cloneName = newName
        )
    }
    
    /**
     * Creates a new instance with updated badge color
     */
    fun updateBadgeColor(colorHex: String): CloneInfo {
        return this.copy(
            badgeColorHex = colorHex
        )
    }
    
    /**
     * Creates a new instance with updated notification setting
     */
    fun updateNotificationSetting(enabled: Boolean): CloneInfo {
        return this.copy(
            isNotificationsEnabled = enabled
        )
    }
    
    /**
     * Creates a new instance with updated custom icon
     */
    fun updateCustomIcon(iconPath: String?): CloneInfo {
        return this.copy(
            customIcon = iconPath
        )
    }
}