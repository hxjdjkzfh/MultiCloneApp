package com.multiclone.app.data.model

import android.graphics.Bitmap

/**
 * Data class representing an installed application.
 */
data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Bitmap? = null,
    val versionName: String = "",
    val versionCode: Long = 0,
    val sizeInMB: Float = 0f,
    val isSystem: Boolean = false,
    val installTime: Long = 0,
    val lastUpdateTime: Long = 0
)
