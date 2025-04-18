package com.multiclone.app.data.model

import android.graphics.drawable.Drawable

/**
 * Data class representing information about an installed app
 *
 * @property packageName The package name of the app
 * @property appName The display name of the app
 * @property icon The app icon drawable
 * @property versionName The version name of the app
 * @property versionCode The version code of the app
 * @property isSystem Whether the app is a system app
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val versionName: String,
    val versionCode: Long,
    val isSystem: Boolean
)