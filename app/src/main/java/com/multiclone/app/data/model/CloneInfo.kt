package com.multiclone.app.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

/**
 * Data class representing a cloned app
 */
@Parcelize
@Serializable
data class CloneInfo(
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val originalAppName: String,
    val cloneName: String,
    val versionName: String,
    val versionCode: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis(),
    val isRunning: Boolean = false,
    
    // Custom icon and original icon are marked as @Transient because Bitmap can't be serialized
    @Transient
    val customIcon: Bitmap? = null,
    
    @Transient
    val originalIcon: Bitmap? = null
) : Parcelable {
    companion object {
        /**
         * Creates a new CloneInfo from an AppInfo
         */
        fun fromAppInfo(appInfo: AppInfo, cloneName: String): CloneInfo {
            return CloneInfo(
                packageName = appInfo.packageName,
                originalAppName = appInfo.appName,
                cloneName = cloneName,
                versionName = appInfo.versionName,
                versionCode = appInfo.versionCode,
                originalIcon = appInfo.appIcon
            )
        }
    }
}