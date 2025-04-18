package com.multiclone.app.data.model

import android.graphics.drawable.Drawable

/**
 * Data class representing information about an installed application
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val versionCode: Long,
    val icon: Drawable,
    val isSystemApp: Boolean
)