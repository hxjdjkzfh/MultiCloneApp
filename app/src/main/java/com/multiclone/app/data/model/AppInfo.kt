package com.multiclone.app.data.model

import android.graphics.drawable.Drawable

/**
 * Data class containing information about an installed application.
 * 
 * @property packageName The package name of the app (com.example.app)
 * @property appName The display name of the app
 * @property versionName The version name of the app (e.g., "1.0.0")
 * @property versionCode The version code of the app (e.g., 10)
 * @property appIcon The icon drawable for the app, or null if unavailable
 * @property isSystemApp Whether the app is a system app
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val appIcon: Drawable? = null,
    val isSystemApp: Boolean = false
)