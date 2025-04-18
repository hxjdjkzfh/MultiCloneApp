package com.multiclone.app

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.core.virtualization.CloneManagerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Proxy activity that handles the launching of cloned apps
 * This is used as an intermediary to set up the virtualization environment
 * before launching the actual app UI
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    private val TAG = "CloneProxyActivity"
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    private var cloneManagerService: CloneManagerService? = null
    private var bound = false
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CloneManagerService.LocalBinder
            cloneManagerService = binder.getService()
            bound = true
            
            // Now that we're connected to the service, proceed with app launch
            handleAppLaunch()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            cloneManagerService = null
            bound = false
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Bind to the CloneManagerService
        Intent(this, CloneManagerService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
        
        // Start the service if it's not already running
        startService(Intent(this, CloneManagerService::class.java))
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
    }
    
    /**
     * Handle the app launch once we're connected to the service
     */
    private fun handleAppLaunch() {
        val cloneId = intent.getStringExtra("cloneId")
        
        if (cloneId.isNullOrEmpty()) {
            showError("No clone ID provided")
            finish()
            return
        }
        
        lifecycleScope.launch {
            val cloneInfo = cloneRepository.getCloneById(cloneId)
            
            if (cloneInfo == null) {
                showError("Clone not found")
                finish()
                return@launch
            }
            
            // Start the clone session
            val success = cloneManagerService?.startCloneSession(cloneInfo) ?: false
            
            if (!success) {
                showError("Failed to start clone")
                finish()
                return@launch
            }
            
            // Update the last used time
            cloneRepository.updateLastUsedTime(cloneId)
            
            // In a real implementation, we would now launch the actual app UI
            // For this demonstration, we'll just show a message and finish
            showMessage("Successfully launched ${cloneInfo.displayName}")
            Log.d(TAG, "Clone launched: ${cloneInfo.displayName}")
            
            // Finish the proxy activity and return to the previous screen
            finish()
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
        Log.e(TAG, message)
    }
    
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}