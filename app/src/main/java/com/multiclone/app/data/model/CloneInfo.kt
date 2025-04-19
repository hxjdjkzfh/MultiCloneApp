package com.multiclone.app.data.model

import java.util.Date
import java.util.UUID

/**
 * Represents information about a cloned app
 */
data class CloneInfo(
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val displayName: String,
    val storagePath: String = "",
    val creationDate: Date = Date(),
    val lastUsedDate: Date = Date(),
    val notificationsEnabled: Boolean = true,
    val customIconPath: String = ""
) {
    companion object {
        /**
         * Convert to a map for storage
         */
        fun toMap(cloneInfo: CloneInfo): Map<String, Any> {
            return mapOf(
                "id" to cloneInfo.id,
                "packageName" to cloneInfo.packageName,
                "displayName" to cloneInfo.displayName,
                "storagePath" to cloneInfo.storagePath,
                "creationDate" to cloneInfo.creationDate.time,
                "lastUsedDate" to cloneInfo.lastUsedDate.time,
                "notificationsEnabled" to cloneInfo.notificationsEnabled,
                "customIconPath" to cloneInfo.customIconPath
            )
        }
        
        /**
         * Create from a map from storage
         */
        fun fromMap(map: Map<String, Any>): CloneInfo {
            return CloneInfo(
                id = map["id"] as String,
                packageName = map["packageName"] as String,
                displayName = map["displayName"] as String,
                storagePath = map["storagePath"] as String,
                creationDate = Date(map["creationDate"] as Long),
                lastUsedDate = Date(map["lastUsedDate"] as Long),
                notificationsEnabled = map["notificationsEnabled"] as Boolean,
                customIconPath = map["customIconPath"] as String
            )
        }
    }
}