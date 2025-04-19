package com.multiclone.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing app clones
 */
@Singleton
class CloneRepository @Inject constructor(
    private val context: Context
) {
    // Encrypted shared preferences for storing sensitive app data
    private val securePrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            "secure_clone_data",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    // Regular preferences for non-sensitive app data
    private val prefs by lazy {
        context.getSharedPreferences("clone_app_prefs", Context.MODE_PRIVATE)
    }
    
    // In-memory cache of clone info
    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val clones: Flow<List<CloneInfo>> = _clones.asStateFlow()
    
    /**
     * Initialize the repository with stored data
     */
    init {
        // Load clones from storage
        loadClones()
    }
    
    /**
     * Load clone data from secure storage
     */
    private fun loadClones() {
        try {
            val clonesJson = securePrefs.getString("clones", null) ?: return
            
            val clonesList = mutableListOf<CloneInfo>()
            val jsonArray = JSONArray(clonesJson)
            
            for (i in 0 until jsonArray.length()) {
                val cloneObj = jsonArray.getJSONObject(i)
                
                // Extract basic properties
                val id = cloneObj.getString("id")
                val packageName = cloneObj.getString("packageName")
                val cloneName = cloneObj.getString("cloneName")
                val customIcon = cloneObj.optBoolean("customIcon", false)
                val iconPath = cloneObj.optString("iconPath", null)
                val creationTime = cloneObj.getLong("creationTime")
                val lastLaunchTime = cloneObj.optLong("lastLaunchTime", 0L)
                val launchCount = cloneObj.optInt("launchCount", 0)
                val isRunning = cloneObj.optBoolean("isRunning", false)
                
                // Extract custom settings if present
                val customSettings = mutableMapOf<String, String>()
                if (cloneObj.has("settings")) {
                    val settingsObj = cloneObj.getJSONObject("settings")
                    val keys = settingsObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        customSettings[key] = settingsObj.getString(key)
                    }
                }
                
                // Try to load the icon
                val icon = if (iconPath != null && iconPath.isNotEmpty()) {
                    try {
                        val iconFile = File(iconPath)
                        if (iconFile.exists()) {
                            // In a real implementation, we would load the drawable from the file
                            null
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to load icon for clone: $id")
                        null
                    }
                } else {
                    // Try to get the original app's icon
                    try {
                        context.packageManager.getApplicationIcon(packageName)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to load original app icon for clone: $id")
                        null
                    }
                }
                
                val cloneInfo = CloneInfo(
                    id = id,
                    originalPackageName = packageName,
                    cloneName = cloneName,
                    customIcon = customIcon,
                    iconPath = if (iconPath.isNullOrEmpty()) null else iconPath,
                    creationTime = creationTime,
                    lastLaunchTime = lastLaunchTime,
                    launchCount = launchCount,
                    isRunning = isRunning,
                    customSettings = customSettings,
                    icon = icon
                )
                
                clonesList.add(cloneInfo)
            }
            
            _clones.value = clonesList
            
            Timber.d("Loaded ${clonesList.size} clones from storage")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load clones from storage")
        }
    }
    
    /**
     * Save clone data to secure storage
     */
    private fun saveClones() {
        try {
            val clonesList = _clones.value
            val jsonArray = JSONArray()
            
            for (clone in clonesList) {
                val cloneObj = JSONObject()
                
                // Add basic properties
                cloneObj.put("id", clone.id)
                cloneObj.put("packageName", clone.originalPackageName)
                cloneObj.put("cloneName", clone.cloneName)
                cloneObj.put("customIcon", clone.customIcon)
                cloneObj.put("iconPath", clone.iconPath ?: "")
                cloneObj.put("creationTime", clone.creationTime)
                cloneObj.put("lastLaunchTime", clone.lastLaunchTime)
                cloneObj.put("launchCount", clone.launchCount)
                cloneObj.put("isRunning", clone.isRunning)
                
                // Add custom settings if present
                if (clone.customSettings.isNotEmpty()) {
                    val settingsObj = JSONObject()
                    for ((key, value) in clone.customSettings) {
                        settingsObj.put(key, value)
                    }
                    cloneObj.put("settings", settingsObj)
                }
                
                jsonArray.put(cloneObj)
            }
            
            securePrefs.edit().putString("clones", jsonArray.toString()).apply()
            
            Timber.d("Saved ${clonesList.size} clones to storage")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save clones to storage")
        }
    }
    
    /**
     * Get information about all installed apps
     */
    fun getInstalledApps(): List<AppInfo> {
        try {
            val packageManager = context.packageManager
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            
            return installedApps.mapNotNull { appInfo ->
                try {
                    // Skip system apps that can't be launched
                    if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) &&
                        packageManager.getLaunchIntentForPackage(appInfo.packageName) == null) {
                        return@mapNotNull null
                    }
                    
                    // Get package info
                    val packageInfo = packageManager.getPackageInfo(appInfo.packageName, 0)
                    
                    // Create app info
                    AppInfo(
                        packageName = appInfo.packageName,
                        appName = packageManager.getApplicationLabel(appInfo).toString(),
                        versionName = packageInfo.versionName ?: "",
                        versionCode = packageInfo.versionCode.toLong(),
                        isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0),
                        installTime = packageInfo.firstInstallTime,
                        updateTime = packageInfo.lastUpdateTime,
                        appSize = try {
                            packageManager.getApplicationInfo(appInfo.packageName, 0).sourceDir?.let {
                                File(it).length()
                            } ?: 0L
                        } catch (e: Exception) {
                            0L
                        },
                        icon = packageManager.getApplicationIcon(appInfo)
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Failed to get info for app: ${appInfo.packageName}")
                    null
                }
            }.sortedBy { it.appName }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get installed apps")
            return emptyList()
        }
    }
    
    /**
     * Get a list of all clones
     */
    fun getAllClones(): List<CloneInfo> {
        return _clones.value
    }
    
    /**
     * Get a clone by its ID
     */
    fun getCloneById(cloneId: String): CloneInfo? {
        return _clones.value.find { it.id == cloneId }
    }
    
    /**
     * Create a new clone
     */
    fun createClone(cloneInfo: CloneInfo): Boolean {
        try {
            // Add the clone to the list
            val updatedClones = _clones.value.toMutableList()
            updatedClones.add(cloneInfo)
            _clones.value = updatedClones
            
            // Save the clones to storage
            saveClones()
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to create clone: ${cloneInfo.id}")
            return false
        }
    }
    
    /**
     * Update an existing clone
     */
    fun updateClone(cloneInfo: CloneInfo): Boolean {
        try {
            // Find the clone in the list
            val index = _clones.value.indexOfFirst { it.id == cloneInfo.id }
            if (index == -1) {
                Timber.e("Clone not found for update: ${cloneInfo.id}")
                return false
            }
            
            // Update the clone
            val updatedClones = _clones.value.toMutableList()
            updatedClones[index] = cloneInfo
            _clones.value = updatedClones
            
            // Save the clones to storage
            saveClones()
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to update clone: ${cloneInfo.id}")
            return false
        }
    }
    
    /**
     * Delete a clone
     */
    fun deleteClone(cloneId: String): Boolean {
        try {
            // Find the clone in the list
            val clone = _clones.value.find { it.id == cloneId } ?: return false
            
            // Remove the clone from the list
            val updatedClones = _clones.value.toMutableList()
            updatedClones.remove(clone)
            _clones.value = updatedClones
            
            // Delete the icon file if it exists
            clone.iconPath?.let { path ->
                try {
                    val iconFile = File(path)
                    if (iconFile.exists()) {
                        iconFile.delete()
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to delete icon file for clone: $cloneId")
                }
            }
            
            // Save the clones to storage
            saveClones()
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete clone: $cloneId")
            return false
        }
    }
    
    /**
     * Update the launch statistics for a clone
     */
    fun updateLaunchStats(cloneId: String): Boolean {
        try {
            // Find the clone in the list
            val clone = _clones.value.find { it.id == cloneId } ?: return false
            
            // Update the clone's launch stats
            val updatedClone = clone.withUpdatedLaunchStats()
            return updateClone(updatedClone)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update launch stats for clone: $cloneId")
            return false
        }
    }
    
    /**
     * Update the running status of a clone
     */
    fun updateCloneRunningStatus(cloneId: String, isRunning: Boolean): Boolean {
        try {
            // Find the clone in the list
            val clone = _clones.value.find { it.id == cloneId } ?: return false
            
            // Update the clone's running status
            val updatedClone = clone.withUpdatedRunningStatus(isRunning)
            return updateClone(updatedClone)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update running status for clone: $cloneId")
            return false
        }
    }
    
    /**
     * Get a list of clones for a specific package
     */
    fun getClonesForPackage(packageName: String): List<CloneInfo> {
        return _clones.value.filter { it.originalPackageName == packageName }
    }
    
    /**
     * Get the count of clones for a specific package
     */
    fun getCloneCountForPackage(packageName: String): Int {
        return _clones.value.count { it.originalPackageName == packageName }
    }
    
    /**
     * Get the count of running clones
     */
    fun getRunningCloneCount(): Int {
        return _clones.value.count { it.isRunning }
    }
    
    /**
     * Check if a clone is running
     */
    fun isCloneRunning(cloneId: String): Boolean {
        return _clones.value.find { it.id == cloneId }?.isRunning ?: false
    }
}