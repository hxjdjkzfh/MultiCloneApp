package com.multiclone.app.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data class representing an installed app
 */
@Parcelize
@Serializable
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean = false,
    
    // Icon is marked as @Transient because it can't be serialized
    @Transient
    val appIcon: Bitmap? = null
) : Parcelable