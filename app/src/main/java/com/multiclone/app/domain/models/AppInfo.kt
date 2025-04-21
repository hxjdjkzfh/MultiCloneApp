package com.multiclone.app.domain.models

import android.graphics.drawable.Drawable

/**
 * Data class representing information about an installed application
 *
 * @property packageName The application's package name
 * @property appName The application's display name
 * @property icon The application's icon
 * @property versionName The application's version name
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val versionName: String
)