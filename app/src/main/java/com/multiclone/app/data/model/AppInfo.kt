package com.multiclone.app.data.model

import android.graphics.Bitmap

/**
 * Represents information about an installed application
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val icon: Bitmap?,
    val isSystemApp: Boolean,
    val installTime: Long,
    val lastUpdateTime: Long
)