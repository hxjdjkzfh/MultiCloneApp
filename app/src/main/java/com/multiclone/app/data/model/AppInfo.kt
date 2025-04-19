package com.multiclone.app.data.model

import android.graphics.drawable.Drawable

/**
 * Represents information about an application that can be cloned
 */
data class AppInfo(
    // Package name (unique identifier for the app)
    val packageName: String,
    
    // User-friendly name of the app
    val appName: String,
    
    // App icon as drawable
    val icon: Drawable?,
    
    // Full path to the app's APK file
    val sourceDir: String,
    
    // Whether this is a system app
    val isSystem: Boolean
)