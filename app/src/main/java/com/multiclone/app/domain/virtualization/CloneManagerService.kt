package com.multiclone.app.domain.virtualization

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.multiclone.app.MainActivity
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Service for managing cloned applications in the background
 */
@AndroidEntryPoint
class CloneManagerService : Service() {

    companion object {
        private const val TAG = "CloneManagerService"
        private const val NOTIFICATION_ID = 100
        private const val CHANNEL_ID = "clone_manager_channel"
    }

    @Inject
    lateinit var cloneRepository: CloneRepository

    @Inject
    lateinit var cloneEnvironment: CloneEnvironment

    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private val activeClones = ConcurrentHashMap<String, Long>() // CloneId -> Last activity timestamp
    private val binder = LocalBinder()

    // Binder for local service binding
    inner class LocalBinder : Binder() {
        fun getService(): CloneManagerService = this@CloneManagerService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "CloneManagerService created")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service received start command")
        
        // Process the intent
        intent?.let { processIntent(it) }

        // Load active clones
        serviceScope.launch {
            loadActiveClones()
        }

        // If the service is killed, it should be restarted
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        Log.d(TAG, "CloneManagerService destroyed")
        serviceScope.cancel()
        super.onDestroy()
    }

    /**
     * Process intents received by the service
     */
    private fun processIntent(intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Processing intent action: $action")
        
        when (action) {
            "com.multiclone.app.ACTION_START_CLONE" -> {
                val cloneId = intent.getStringExtra("clone_id") ?: return
                serviceScope.launch {
                    startClone(cloneId)
                }
            }
            "com.multiclone.app.ACTION_STOP_CLONE" -> {
                val cloneId = intent.getStringExtra("clone_id") ?: return
                serviceScope.launch {
                    stopClone(cloneId)
                }
            }
            "com.multiclone.app.ACTION_REFRESH_CLONES" -> {
                serviceScope.launch {
                    refreshClones()
                }
            }
        }
    }

    /**
     * Load active clones from the repository
     */
    private suspend fun loadActiveClones() {
        try {
            // Clear current active clones
            activeClones.clear()
            
            // Get recently used clones
            val allClones = cloneRepository.getAllClones()
                .collect { clones ->
                    Log.d(TAG, "Loaded ${clones.size} clones")
                    
                    // Consider recently used clones as "active"
                    // (within the last hour, for example)
                    val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
                    
                    clones.forEach { clone ->
                        if (clone.lastUsedTime > oneHourAgo) {
                            activeClones[clone.id] = clone.lastUsedTime
                        }
                    }
                    
                    Log.d(TAG, "${activeClones.size} clones are active")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading active clones", e)
        }
    }

    /**
     * Start a cloned application
     */
    private suspend fun startClone(cloneId: String) {
        try {
            Log.d(TAG, "Starting clone: $cloneId")
            
            // Get clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: run {
                Log.e(TAG, "Clone not found: $cloneId")
                return
            }
            
            // Get environment ID for this clone, or create one if it doesn't exist
            val environmentId = cloneEnvironment.getEnvironmentIdForClone(cloneId) ?: run {
                Log.d(TAG, "No environment found for clone $cloneId, creating one...")
                
                // Create a new virtual environment for this clone
                val createResult = virtualAppEngine.createVirtualEnvironment(
                    packageName = cloneInfo.packageName,
                    cloneId = cloneId
                )
                
                if (createResult.isFailure) {
                    Log.e(TAG, "Failed to create virtual environment: ${createResult.exceptionOrNull()?.message}")
                    return
                }
                
                createResult.getOrNull() ?: return
            }
            
            // Register as active
            activeClones[cloneId] = System.currentTimeMillis()
            
            // Update notification with the number of active clones
            updateNotification()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting clone", e)
        }
    }

    /**
     * Stop a cloned application
     */
    private suspend fun stopClone(cloneId: String) {
        try {
            Log.d(TAG, "Stopping clone: $cloneId")
            
            // Remove from active clones
            activeClones.remove(cloneId)
            
            // Update notification
            updateNotification()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping clone", e)
        }
    }

    /**
     * Refresh the list of clones
     */
    private suspend fun refreshClones() {
        try {
            Log.d(TAG, "Refreshing clones")
            loadActiveClones()
            updateNotification()
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing clones", e)
        }
    }

    /**
     * Create the notification channel
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Clone Manager"
            val descriptionText = "Manages running cloned applications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Create the foreground notification
     */
    private fun createNotification(): Notification {
        val pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MultiClone Manager")
            .setContentText("Managing ${activeClones.size} cloned apps")
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setContentIntent(pendingIntent)
            .build()
    }

    /**
     * Update the foreground notification
     */
    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    /**
     * Get the list of active clones
     */
    fun getActiveClones(): List<String> {
        return activeClones.keys().toList()
    }

    /**
     * Check if a clone is active
     */
    fun isCloneActive(cloneId: String): Boolean {
        return activeClones.containsKey(cloneId)
    }
}