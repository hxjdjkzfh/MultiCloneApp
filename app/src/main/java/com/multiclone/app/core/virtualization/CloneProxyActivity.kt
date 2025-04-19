package com.multiclone.app.core.virtualization

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Proxy activity for launching cloned apps
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "CloneProxyActivity"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_CLONE_ID = "extra_clone_id"
        const val EXTRA_TARGET_ACTIVITY = "extra_target_activity"
    }
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    private var virtualizationService: VirtualizationService? = null
    private var bound = false
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as VirtualizationService.LocalBinder
            virtualizationService = binder.getService()
            bound = true
            
            // Once bound, launch the cloned app
            launchClone()
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            virtualizationService = null
            bound = false
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Extract parameters from intent
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        val cloneId = intent.getStringExtra(EXTRA_CLONE_ID)
        val targetActivity = intent.getStringExtra(EXTRA_TARGET_ACTIVITY)
        
        if (packageName == null || cloneId == null) {
            Log.e(TAG, "Missing required parameters: packageName=$packageName, cloneId=$cloneId")
            finish()
            return
        }
        
        Log.d(TAG, "Launching clone: $packageName, id: $cloneId")
        
        // Bind to the virtualization service
        Intent(this, VirtualizationService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
        
        // Create a shortcut for this cloned app (if it doesn't exist yet)
        CoroutineScope(Dispatchers.IO).launch {
            createShortcut(packageName, cloneId)
        }
    }
    
    private fun launchClone() {
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return
        val cloneId = intent.getStringExtra(EXTRA_CLONE_ID) ?: return
        val targetActivity = intent.getStringExtra(EXTRA_TARGET_ACTIVITY)
        
        // Prepare the virtual environment
        virtualizationService?.prepareVirtualApp(packageName, cloneId, targetActivity)
        
        // In a real implementation, we would:
        // 1. Create a new process with the virtualized environment
        // 2. Set up IPC hooks for virtualization
        // 3. Launch the actual app in the virtualized environment
        
        // For this example, we'll just start a placeholder intent
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            // In a real implementation, this would be patched to run in the virtual environment
            startActivity(launchIntent)
        } else {
            Log.e(TAG, "Could not find launch intent for package: $packageName")
        }
        
        // Update usage time for this clone
        CoroutineScope(Dispatchers.IO).launch {
            cloneRepository.updateLastUsedTime(cloneId)
        }
        
        // Finish the proxy activity
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Unbind from the service
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
    }
    
    /**
     * Create a shortcut for the cloned app on the launcher
     */
    private suspend fun createShortcut(packageName: String, cloneId: String) {
        try {
            // Get clone info from repository
            val clones = cloneRepository.clones.replayCache.firstOrNull() ?: return
            val clone = clones.find { it.id == cloneId } ?: return
            
            // Get app info
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appLabel = packageManager.getApplicationLabel(appInfo).toString()
            
            // Create intent for launching this clone
            val launchIntent = Intent(this, CloneProxyActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_CLONE_ID, cloneId)
                action = Intent.ACTION_VIEW
            }
            
            // Build the shortcut
            val shortcutInfo = ShortcutInfoCompat.Builder(this, "clone_$cloneId")
                .setShortLabel(clone.displayName)
                .setLongLabel("${clone.displayName} (MultiClone)")
                .setIcon(IconCompat.createWithResource(this, android.R.drawable.sym_def_app_icon))
                .setIntent(launchIntent)
                .build()
            
            // Create the shortcut
            ShortcutManagerCompat.requestPinShortcut(this, shortcutInfo, null)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating shortcut", e)
        }
    }
}