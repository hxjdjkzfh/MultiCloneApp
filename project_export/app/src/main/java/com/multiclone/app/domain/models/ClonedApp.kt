package com.multiclone.app.domain.models

import kotlinx.serialization.Serializable

/**
 * Model representing a cloned application instance
 */
@Serializable
data class ClonedApp(
    // Unique identifier for this clone instance
    val cloneId: String,
    
    // The original package name of the app
    val originalPackageName: String,
    
    // User-defined name for this clone
    val displayName: String,
    
    // Creation timestamp
    val createdAt: Long = System.currentTimeMillis(),
    
    // Last used timestamp
    val lastUsed: Long = 0,
    
    // Whether the cloned app is currently running
    val isRunning: Boolean = false,
    
    // Whether this clone uses isolated storage
    val storageIsolated: Boolean = true,
    
    // App specific settings
    val settings: Map<String, String> = emptyMap(),
    
    // Custom icon resource (if any)
    val customIconPath: String? = null,
    
    // Clone index (useful for multiple clones of the same app)
    val cloneIndex: Int = 0
)