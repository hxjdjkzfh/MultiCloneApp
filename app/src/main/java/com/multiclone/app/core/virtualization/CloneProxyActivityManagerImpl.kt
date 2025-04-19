package com.multiclone.app.core.virtualization

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CloneProxyActivityManager that handles the proxying
 * of activities for cloned apps to maintain isolation.
 */
@Singleton
class CloneProxyActivityManagerImpl @Inject constructor(
    private val context: Context
) : CloneProxyActivityManager {

    companion object {
        private const val PROXY_REGISTRY_FILE = "proxy_registry.json"
        private const val CLONE_INTENT_EXTRA = "com.multiclone.app.CLONE_ID"
        private const val CLONE_PACKAGE_EXTRA = "com.multiclone.app.ORIGINAL_PACKAGE"
    }
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Registers proxy activities for a cloned app.
     */
    override suspend fun registerProxies(packageName: String, cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Registering proxy activities for clone ${cloneInfo.id} (${packageName})")
            
            // For non-root implementation, we don't actually create new activities in the system
            // but instead maintain a registry that maps clone IDs to their original packages
            
            // Get app's main activities
            val launchableActivities = getLaunchableActivities(packageName)
            
            if (launchableActivities.isEmpty()) {
                Timber.e("No launchable activities found for $packageName")
                return@withContext false
            }
            
            // Create proxy registry entry
            val registry = ProxyRegistry(
                cloneId = cloneInfo.id,
                originalPackage = packageName,
                activities = launchableActivities
            )
            
            // Save registry to app's private storage
            val registryDir = getProxyRegistryDirectory()
            if (!registryDir.exists() && !registryDir.mkdirs()) {
                Timber.e("Failed to create proxy registry directory")
                return@withContext false
            }
            
            val registryFile = File(registryDir, "${cloneInfo.id}_$PROXY_REGISTRY_FILE")
            val registryJson = json.encodeToString(registry)
            registryFile.writeText(registryJson)
            
            Timber.d("Successfully registered proxy activities for clone ${cloneInfo.id}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error registering proxy activities for clone ${cloneInfo.id}")
            return@withContext false
        }
    }

    /**
     * Unregisters proxy activities for a cloned app.
     */
    override suspend fun unregisterProxies(cloneInfo: CloneInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Unregistering proxy activities for clone ${cloneInfo.id}")
            
            // Remove registry file
            val registryDir = getProxyRegistryDirectory()
            val registryFile = File(registryDir, "${cloneInfo.id}_$PROXY_REGISTRY_FILE")
            
            if (registryFile.exists() && !registryFile.delete()) {
                Timber.e("Failed to delete proxy registry file for clone ${cloneInfo.id}")
                return@withContext false
            }
            
            Timber.d("Successfully unregistered proxy activities for clone ${cloneInfo.id}")
            return@withContext true
        } catch (e: Exception) {
            Timber.e(e, "Error unregistering proxy activities for clone ${cloneInfo.id}")
            return@withContext false
        }
    }

    /**
     * Gets the launch intent for a cloned app.
     */
    override suspend fun getLaunchIntent(cloneInfo: CloneInfo): Intent? = withContext(Dispatchers.IO) {
        try {
            Timber.d("Getting launch intent for clone ${cloneInfo.id}")
            
            // Read proxy registry
            val registryDir = getProxyRegistryDirectory()
            val registryFile = File(registryDir, "${cloneInfo.id}_$PROXY_REGISTRY_FILE")
            
            if (!registryFile.exists()) {
                Timber.e("Proxy registry file not found for clone ${cloneInfo.id}")
                return@withContext null
            }
            
            val registryJson = registryFile.readText()
            val registry = json.decodeFromString<ProxyRegistry>(registryJson)
            
            // Get main activity
            val mainActivity = registry.activities.firstOrNull { it.isLauncher }
            
            if (mainActivity == null) {
                Timber.e("No launcher activity found for clone ${cloneInfo.id}")
                return@withContext null
            }
            
            // Create intent for proxy activity
            val intent = Intent(context, CloneProxyActivity::class.java).apply {
                putExtra(CLONE_INTENT_EXTRA, cloneInfo.id)
                putExtra(CLONE_PACKAGE_EXTRA, registry.originalPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            Timber.d("Successfully created launch intent for clone ${cloneInfo.id}")
            return@withContext intent
        } catch (e: Exception) {
            Timber.e(e, "Error getting launch intent for clone ${cloneInfo.id}")
            return@withContext null
        }
    }
    
    /**
     * Gets the directory for storing proxy registry files.
     */
    private fun getProxyRegistryDirectory(): File {
        return File(context.filesDir, "proxy_registry")
    }
    
    /**
     * Gets launchable activities for a package.
     */
    private fun getLaunchableActivities(packageName: String): List<ActivityInfo> {
        val packageManager = context.packageManager
        val activities = mutableListOf<ActivityInfo>()
        
        // Get launch intent for the package
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        
        if (launchIntent != null) {
            val componentName = launchIntent.component
            if (componentName != null) {
                activities.add(
                    ActivityInfo(
                        className = componentName.className,
                        packageName = componentName.packageName,
                        isLauncher = true
                    )
                )
            }
        }
        
        // For more comprehensive app cloning, we would also get all exported activities,
        // but for simplicity we're just focusing on the launcher activity
        
        return activities
    }
    
    /**
     * Data class representing an activity in the original app.
     */
    @kotlinx.serialization.Serializable
    private data class ActivityInfo(
        val className: String,
        val packageName: String,
        val isLauncher: Boolean = false
    )
    
    /**
     * Data class representing a proxy registry entry.
     */
    @kotlinx.serialization.Serializable
    private data class ProxyRegistry(
        val cloneId: String,
        val originalPackage: String,
        val activities: List<ActivityInfo>
    )
}