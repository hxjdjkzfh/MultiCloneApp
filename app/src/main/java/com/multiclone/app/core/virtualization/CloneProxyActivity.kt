package com.multiclone.app.core.virtualization

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Proxy activity for launching cloned apps.
 * Acts as a bridge between the MultiClone app and the virtualized app instance.
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {

    companion object {
        // Intent extras
        const val EXTRA_CLONE_ID = "clone_id"
        const val EXTRA_PACKAGE_NAME = "package_name"
        
        // Launch modes
        private const val LAUNCH_MODE_DIRECT = 0
        private const val LAUNCH_MODE_SERVICE = 1
    }
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    // Service connections
    private var virtualizationServiceConnection: ServiceConnection? = null
    private var virtualizationService: VirtualizationService? = null
    
    private var cloneManagerServiceConnection: ServiceConnection? = null
    private var cloneManagerService: CloneManagerService? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("CloneProxyActivity created")
        
        // Get parameters from intent
        val cloneId = intent.getStringExtra(EXTRA_CLONE_ID)
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        
        if (cloneId == null || packageName == null) {
            Timber.e("Missing required parameters: cloneId=$cloneId, packageName=$packageName")
            finish()
            return
        }
        
        // Launch the clone
        launchClone(cloneId, packageName)
    }
    
    /**
     * Launches a cloned app.
     * 
     * @param cloneId Clone ID
     * @param packageName Package name
     */
    private fun launchClone(cloneId: String, packageName: String) {
        Timber.d("Launching clone $cloneId for package $packageName")
        
        // Start the virtualization service
        bindVirtualizationService(cloneId)
        
        // Start the clone manager service
        bindCloneManagerService(cloneId)
        
        // Load the clone configuration
        CoroutineScope(Dispatchers.Main).launch {
            val clone = cloneRepository.getCloneById(cloneId)
            
            if (clone == null) {
                Timber.e("Clone not found: $cloneId")
                finish()
                return@launch
            }
            
            // Check if the clone is installed
            if (!virtualAppEngine.isCloneInstalled(clone)) {
                Timber.d("Clone not installed, creating it now")
                val success = virtualAppEngine.createClone(clone)
                if (!success) {
                    Timber.e("Failed to create clone")
                    finish()
                    return@launch
                }
            }
            
            // Get the original app's launch intent
            val configFile = File(getVirtualAppDir(cloneId), "launch_config.properties")
            if (!configFile.exists()) {
                Timber.e("Launch config file not found for clone $cloneId")
                finish()
                return@launch
            }
            
            val properties = configFile.readLines().associate { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    parts[0] to parts[1]
                } else {
                    "" to ""
                }
            }
            
            val mainActivity = properties["activity"]
            if (mainActivity.isNullOrEmpty()) {
                Timber.e("Main activity not found in config file for clone $cloneId")
                finish()
                return@launch
            }
            
            // Create an intent to launch the original app's main activity
            // In a real implementation, this would use advanced techniques to
            // redirect the intent through the virtualized environment
            val intent = Intent().apply {
                component = ComponentName(packageName, mainActivity)
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                
                // Add virtualization parameters
                putExtra("VIRTUALIZED", true)
                putExtra("CLONE_ID", cloneId)
            }
            
            // In a real implementation, we would launch the app through the virtualization layer
            // For demonstration purposes, we'll just show a success message
            Timber.d("Clone launched successfully: $cloneId")
            
            // Update the clone's launch count and timestamp
            val updatedClone = clone.copy(
                launchCount = clone.launchCount + 1,
                lastLaunchedAt = System.currentTimeMillis()
            )
            cloneRepository.updateClone(updatedClone)
            
            // In a real implementation, we would keep this activity running in the background
            // to maintain the virtualization context
            finish()
        }
    }
    
    /**
     * Binds to the virtualization service.
     * 
     * @param cloneId Clone ID
     */
    private fun bindVirtualizationService(cloneId: String) {
        Timber.d("Binding to virtualization service")
        
        virtualizationServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Timber.d("Virtualization service connected")
                virtualizationService = (service as VirtualizationService.LocalBinder).getService()
            }
            
            override fun onServiceDisconnected(name: ComponentName?) {
                Timber.d("Virtualization service disconnected")
                virtualizationService = null
            }
        }
        
        // Start and bind to the service
        val intent = Intent(this, VirtualizationService::class.java).apply {
            action = VirtualizationService.ACTION_START_SERVICE
            putExtra(VirtualizationService.EXTRA_CLONE_ID, cloneId)
        }
        
        startForegroundService(intent)
        bindService(
            intent,
            virtualizationServiceConnection as ServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }
    
    /**
     * Binds to the clone manager service.
     * 
     * @param cloneId Clone ID
     */
    private fun bindCloneManagerService(cloneId: String) {
        Timber.d("Binding to clone manager service")
        
        cloneManagerServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Timber.d("Clone manager service connected")
                cloneManagerService = (service as CloneManagerService.LocalBinder).getService()
            }
            
            override fun onServiceDisconnected(name: ComponentName?) {
                Timber.d("Clone manager service disconnected")
                cloneManagerService = null
            }
        }
        
        // Start and bind to the service
        val intent = Intent(this, CloneManagerService::class.java).apply {
            action = CloneManagerService.ACTION_START_MONITORING
            putExtra(CloneManagerService.EXTRA_CLONE_ID, cloneId)
        }
        
        startService(intent)
        bindService(
            intent,
            cloneManagerServiceConnection as ServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }
    
    /**
     * Gets the virtual app directory for a clone.
     * 
     * @param cloneId Clone ID
     * @return Virtual app directory
     */
    private fun getVirtualAppDir(cloneId: String): File {
        val virtualAppsDir = File(filesDir, "virtual")
        return File(virtualAppsDir, "clone_$cloneId")
    }
    
    override fun onDestroy() {
        Timber.d("CloneProxyActivity destroyed")
        
        // Unbind from services
        virtualizationServiceConnection?.let {
            unbindService(it)
            virtualizationServiceConnection = null
        }
        
        cloneManagerServiceConnection?.let {
            unbindService(it)
            cloneManagerServiceConnection = null
        }
        
        super.onDestroy()
    }
}