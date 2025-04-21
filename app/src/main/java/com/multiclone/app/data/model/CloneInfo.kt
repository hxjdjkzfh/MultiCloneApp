package com.multiclone.app.data.model

import kotlinx.serialization.Serializable

/**
 * Data class containing information about a cloned app.
 * 
 * @property id The unique identifier for this clone
 * @property packageName The package name of the original app
 * @property originalAppName The name of the original app
 * @property customName The user-defined name for this clone
 * @property color The color code for the badge/theme of this clone (hex string)
 * @property useCustomLauncher Whether to use a custom launcher icon
 * @property enableNotifications Whether to enable notifications for this clone
 * @property storeDataInAppFolder Whether to store data in the app's folder
 * @property createTime The timestamp when this clone was created
 * @property lastLaunchedAt The timestamp when this clone was last launched, or null if never launched
 * @property launchCount The number of times this clone has been launched
 */
@Serializable
data class CloneInfo(
    val id: String,
    val packageName: String,
    val originalAppName: String,
    val customName: String,
    val color: String = "#FF5252",
    val useCustomLauncher: Boolean = false,
    val enableNotifications: Boolean = true,
    val storeDataInAppFolder: Boolean = true,
    val createTime: Long = System.currentTimeMillis(),
    val lastLaunchedAt: Long? = null,
    val launchCount: Int = 0
)