package com.multiclone.app.data.model

import android.graphics.drawable.Drawable

/**
 * Represents information about a cloned application
 */
data class CloneInfo(
    // Unique identifier for this clone
    val id: String,
    
    // Package name of the original app
    val packageName: String,
    
    // Custom display name for this clone
    val displayName: String,
    
    // Custom icon for this clone (null means use original app icon)
    val customIcon: Drawable? = null,
    
    // Original app icon
    val originalIcon: Drawable? = null,
    
    // Time when this clone was created
    val creationTime: Long,
    
    // Time when this clone was last used
    val lastUsedTime: Long,
    
    // User-defined color for this clone (used for UI/theming)
    val colorHex: String? = null,
    
    // Whether notifications are enabled for this clone
    val notificationsEnabled: Boolean = true,
    
    // Whether this clone is frozen (won't receive updates)
    val isFrozen: Boolean = false
)