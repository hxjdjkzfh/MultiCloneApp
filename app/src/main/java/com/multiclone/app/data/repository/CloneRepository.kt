package com.multiclone.app.data.repository

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import com.multiclone.app.utils.IconUtils
import com.multiclone.app.utils.PermissionUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloneRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val virtualAppEngine: VirtualAppEngine,
    private val appRepository: AppRepository
) {
    private val _clones = MutableStateFlow<List<CloneInfo>>(emptyList())
    val clones: Flow<List<CloneInfo>> = _clones.asStateFlow()
    
    init {
        // Load stored clones on initialization
        loadClones()
    }
    
    /**
     * Create a clone of an installed application.
     *
     * @param packageName The package name of the app to clone.
     * @param cloneName The custom name for the cloned app.
     * @param customIcon Optional custom icon for the cloned app.
     * @return The created CloneInfo if successful, null otherwise.
     */
    suspend fun createClone(
        packageName: String,
        cloneName: String,
        customIcon: Bitmap? = null
    ): CloneInfo? = withContext(Dispatchers.IO) {
        try {
            // Get original app info
            val originalAppInfo = appRepository.getAppInfo(packageName) ?: return@withContext null
            
            // Generate a unique ID for the clone
            val cloneId = UUID.randomUUID().toString()
            
            // Generate a unique package name for the clone
            val clonePackageName = "${packageName}.clone.$cloneId"
            
            // Create the clone using VirtualAppEngine
            val isCloneCreated = virtualAppEngine.createClone(
                originalPackageName = packageName,
                clonePackageName = clonePackageName
            )
            
            if (!isCloneCreated) {
                return@withContext null
            }
            
            // Save icon if provided, otherwise use the original app icon
            val finalIcon = customIcon ?: originalAppInfo.icon
            finalIcon?.let {
                saveCloneIcon(cloneId, it)
            }
            
            // Create CloneInfo object
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            
            val cloneInfo = CloneInfo(
                id = cloneId,
                originalPackageName = packageName,
                originalAppName = originalAppInfo.name,
                packageName = clonePackageName,
                name = cloneName,
                icon = finalIcon,
                creationDate = currentDate
            )
            
            // Save clone info to persistent storage
            saveCloneInfo(cloneInfo)
            
            // Update the clones list
            val currentClones = _clones.value.toMutableList()
            currentClones.add(cloneInfo)
            _clones.value = currentClones
            
            cloneInfo
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get a list of all created clones.
     *
     * @return List of CloneInfo objects representing created clones.
     */
    fun loadClones() {
        try {
            val clonesDir = File(context.filesDir, "clones")
            if (!clonesDir.exists()) {
                _clones.value = emptyList()
                return
            }
            
            val cloneInfoList = mutableListOf<CloneInfo>()
            clonesDir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    val cloneId = file.name
                    val infoFile = File(file, "info.json")
                    if (infoFile.exists()) {
                        try {
                            // Deserialize the clone info
                            val json = infoFile.readText()
                            // Simple parsing for demonstration
                            // In a real app, you would use a proper JSON library
                            val originalPackageName = extractJsonValue(json, "originalPackageName")
                            val originalAppName = extractJsonValue(json, "originalAppName")
                            val packageName = extractJsonValue(json, "packageName")
                            val name = extractJsonValue(json, "name")
                            val creationDate = extractJsonValue(json, "creationDate")
                            val lastLaunchDate = extractJsonValue(json, "lastLaunchDate")
                            
                            // Load icon if available
                            val iconFile = File(file, "icon.png")
                            val icon = if (iconFile.exists()) {
                                IconUtils.loadBitmapFromFile(iconFile)
                            } else {
                                null
                            }
                            
                            val cloneInfo = CloneInfo(
                                id = cloneId,
                                originalPackageName = originalPackageName,
                                originalAppName = originalAppName,
                                packageName = packageName,
                                name = name,
                                icon = icon,
                                creationDate = creationDate,
                                lastLaunchDate = lastLaunchDate.takeIf { it.isNotEmpty() }
                            )
                            
                            cloneInfoList.add(cloneInfo)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            
            _clones.value = cloneInfoList
        } catch (e: Exception) {
            e.printStackTrace()
            _clones.value = emptyList()
        }
    }
    
    /**
     * Launch a cloned app.
     *
     * @param cloneInfo The CloneInfo of the clone to launch.
     * @return True if the clone was launched successfully, false otherwise.
     */
    suspend fun launchClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = virtualAppEngine.launchClone(cloneInfo.packageName)
            
            if (result) {
                // Update last launch date
                updateCloneLastLaunchDate(cloneInfo)
            }
            
            result
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Delete a cloned app.
     *
     * @param cloneInfo The CloneInfo of the clone to delete.
     * @return True if the clone was deleted successfully, false otherwise.
     */
    suspend fun deleteClone(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val isDeleted = virtualAppEngine.deleteClone(cloneInfo.packageName)
            
            if (isDeleted) {
                // Delete clone info from persistent storage
                deleteCloneInfo(cloneInfo.id)
                
                // Update the clones list
                val currentClones = _clones.value.toMutableList()
                currentClones.removeAll { it.id == cloneInfo.id }
                _clones.value = currentClones
            }
            
            isDeleted
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Create a home screen shortcut for a cloned app.
     *
     * @param cloneInfo The CloneInfo of the clone to create a shortcut for.
     * @param context The application context.
     * @return True if the shortcut was created successfully, false otherwise.
     */
    suspend fun createShortcut(cloneInfo: CloneInfo, context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!PermissionUtils.canCreateShortcuts(context)) {
                return@withContext false
            }
            
            val shortcutIntent = Intent(context, Class.forName("com.multiclone.app.CloneProxyActivity"))
            shortcutIntent.action = Intent.ACTION_VIEW
            shortcutIntent.putExtra("clone_package_name", cloneInfo.packageName)
            shortcutIntent.putExtra("clone_id", cloneInfo.id)
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            val icon = cloneInfo.icon ?: return@withContext false
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Use ShortcutManager for Android 8.0+
                val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as android.content.pm.ShortcutManager
                
                if (!shortcutManager.isRequestPinShortcutSupported) {
                    return@withContext false
                }
                
                val iconBadge = IconUtils.createIconAdaptiveBadge(icon)
                val shortcutInfo = android.content.pm.ShortcutInfo.Builder(context, cloneInfo.id)
                    .setShortLabel(cloneInfo.name)
                    .setLongLabel("Launch ${cloneInfo.name}")
                    .setIcon(android.graphics.drawable.Icon.createWithBitmap(iconBadge))
                    .setIntent(shortcutIntent)
                    .build()
                
                return@withContext shortcutManager.requestPinShortcut(shortcutInfo, null)
            } else {
                // Legacy shortcut creation for Android 7.1 and below
                val addShortcutIntent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
                addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, cloneInfo.name)
                addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon)
                addShortcutIntent.putExtra("duplicate", false)
                
                context.sendBroadcast(addShortcutIntent)
                return@withContext true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Save clone information to persistent storage.
     *
     * @param cloneInfo The CloneInfo to save.
     */
    private fun saveCloneInfo(cloneInfo: CloneInfo) {
        try {
            val clonesDir = File(context.filesDir, "clones")
            if (!clonesDir.exists()) {
                clonesDir.mkdirs()
            }
            
            val cloneDir = File(clonesDir, cloneInfo.id)
            if (!cloneDir.exists()) {
                cloneDir.mkdirs()
            }
            
            // Save clone info as JSON
            val infoFile = File(cloneDir, "info.json")
            // Simple JSON serialization for demonstration
            // In a real app, you would use a proper JSON library
            val json = """
                {
                    "id": "${cloneInfo.id}",
                    "originalPackageName": "${cloneInfo.originalPackageName}",
                    "originalAppName": "${cloneInfo.originalAppName}",
                    "packageName": "${cloneInfo.packageName}",
                    "name": "${cloneInfo.name}",
                    "creationDate": "${cloneInfo.creationDate}",
                    "lastLaunchDate": "${cloneInfo.lastLaunchDate ?: ""}"
                }
            """.trimIndent()
            
            infoFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update the last launch date for a clone.
     *
     * @param cloneInfo The CloneInfo to update.
     */
    private fun updateCloneLastLaunchDate(cloneInfo: CloneInfo) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            
            val updatedCloneInfo = cloneInfo.copy(lastLaunchDate = currentDate)
            
            // Save updated info
            saveCloneInfo(updatedCloneInfo)
            
            // Update the clones list
            val currentClones = _clones.value.toMutableList()
            val index = currentClones.indexOfFirst { it.id == cloneInfo.id }
            if (index >= 0) {
                currentClones[index] = updatedCloneInfo
                _clones.value = currentClones
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Delete clone information from persistent storage.
     *
     * @param cloneId The ID of the clone to delete.
     */
    private fun deleteCloneInfo(cloneId: String) {
        try {
            val cloneDir = File(context.filesDir, "clones/${cloneId}")
            if (cloneDir.exists()) {
                cloneDir.deleteRecursively()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Save the icon for a clone.
     *
     * @param cloneId The ID of the clone.
     * @param icon The icon to save.
     */
    private fun saveCloneIcon(cloneId: String, icon: Bitmap) {
        try {
            val cloneDir = File(context.filesDir, "clones/${cloneId}")
            if (!cloneDir.exists()) {
                cloneDir.mkdirs()
            }
            
            val iconFile = File(cloneDir, "icon.png")
            IconUtils.saveBitmapToFile(icon, iconFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Extract a value from a JSON string.
     *
     * @param json The JSON string.
     * @param key The key to extract.
     * @return The extracted value.
     */
    private fun extractJsonValue(json: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        val matchResult = pattern.find(json)
        return matchResult?.groupValues?.get(1) ?: ""
    }
}
