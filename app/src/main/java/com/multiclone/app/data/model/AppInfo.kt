package com.multiclone.app.data.model

import android.graphics.drawable.Drawable

/**
 * Represents information about an installed app
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val appIcon: Drawable
)