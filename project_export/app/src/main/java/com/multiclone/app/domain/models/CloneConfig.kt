package com.multiclone.app.domain.models

import android.graphics.Bitmap

/**
 * Configuration for creating a new app clone
 *
 * @property customName Optional custom name for the cloned app
 * @property customIcon Optional custom icon for the cloned app
 * @property isolationLevel Level of isolation for the cloned app
 */
data class CloneConfig(
    val customName: String? = null,
    val customIcon: Bitmap? = null,
    val isolationLevel: IsolationLevel = IsolationLevel.STANDARD
)

/**
 * Defines the isolation level for a cloned app
 */
enum class IsolationLevel {
    /**
     * Basic isolation - separate storage only
     */
    BASIC,
    
    /**
     * Standard isolation - separate storage and user data, but shared system settings
     */
    STANDARD,
    
    /**
     * Maximum isolation - completely isolated environment with separate everything
     */
    MAXIMUM
}