package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents information about an installed application.
 */
@Parcelize
@Serializable
data class AppInfo(
    /**
     * Package name (unique identifier) of the application.
     */
    val packageName: String,
    
    /**
     * Display name of the application.
     */
    val appName: String,
    
    /**
     * Version name (human-readable) of the application.
     */
    val versionName: String,
    
    /**
     * Version code (numeric) of the application.
     */
    val versionCode: Long,
    
    /**
     * Whether this is a system application.
     */
    val isSystemApp: Boolean,
    
    /**
     * First installation time in milliseconds since epoch.
     */
    val installTime: Long,
    
    /**
     * Last update time in milliseconds since epoch.
     */
    val lastUpdateTime: Long,
    
    /**
     * Icon drawable of the application.
     * Transient because it cannot be serialized directly.
     */
    @Transient
    val appIcon: @RawValue Drawable? = null
) : Parcelable