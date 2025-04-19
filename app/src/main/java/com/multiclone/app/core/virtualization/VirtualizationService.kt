package com.multiclone.app.core.virtualization

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.multiclone.app.R
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground service that manages the virtualization environment for cloned apps.
 * This service ensures that the virtualization layer remains active when clones are running.
 */
@AndroidEntryPoint
class VirtualizationService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "virtualization_channel"
        
        // Intent actions
        const val ACTION_START_SERVICE = "com.multiclone.app.START_VIRTUALIZATION"
        const val ACTION_STOP_SERVICE = "com.multiclone.app.STOP_VIRTUALIZATION"
        
        // Intent extras
        const val EXTRA_CLONE_ID = "clone_id"
    }
    
    // Service binder
    private val binder = LocalBinder()
    
    // Coroutine scope for background operations
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    
    // Currently active clones
    private val activeClones = mutableSetOf<String>()
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("VirtualizationService created")
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("VirtualizationService started with intent: $intent")
        
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                val cloneId = intent.getStringExtra(EXTRA_CLONE_ID)
                if (cloneId != null) {
                    startVirtualization(cloneId)
                } else {
                    startVirtualization(null)
                }
            }
            ACTION_STOP_SERVICE -> {
                val cloneId = intent.getStringExtra(EXTRA_CLONE_ID)
                if (cloneId != null) {
                    stopVirtualization(cloneId)
                } else {
                    stopVirtualization(null)
                }
            }
            else -> {
                Timber.d("Unknown action: ${intent?.action}")
            }
        }
        
        // Return sticky to restart the service if it's killed
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder {
        Timber.d("VirtualizationService bound")
        return binder
    }
    
    override fun onDestroy() {
        Timber.d("VirtualizationService destroyed")
        super.onDestroy()
    }
    
    /**
     * Starts the virtualization layer
     */
    private fun startVirtualization(cloneId: String?) {
        Timber.d("Starting virtualization service for clone: $cloneId")
        
        // Start as a foreground service to prevent system from killing it
        startForeground(NOTIFICATION_ID, createNotification())
        
        if (cloneId != null) {
            // Add to active clones
            synchronized(activeClones) {
                activeClones.add(cloneId)
                Timber.d("Active clones: $activeClones")
            }
            
            // Initialize the virtualization layer for this clone
            serviceScope.launch {
                try {
                    val clone = cloneRepository.getCloneById(cloneId)
                    if (clone == null) {
                        Timber.e("Clone not found: $cloneId")
                        return@launch
                    }
                    
                    // Check if the clone is installed
                    if (!virtualAppEngine.isCloneInstalled(clone)) {
                        Timber.e("Clone is not installed: $cloneId")
                        return@launch
                    }
                    
                    // Additional initialization if needed
                    
                    Timber.d("Virtualization started for clone: $cloneId")
                } catch (e: Exception) {
                    Timber.e(e, "Error starting virtualization for clone: $cloneId")
                }
            }
        } else {
            // Starting the general virtualization layer
            Timber.d("Starting general virtualization layer")
        }
    }
    
    /**
     * Stops the virtualization layer
     */
    private fun stopVirtualization(cloneId: String?) {
        if (cloneId != null) {
            Timber.d("Stopping virtualization for clone: $cloneId")
            
            // Remove from active clones
            synchronized(activeClones) {
                activeClones.remove(cloneId)
                Timber.d("Active clones: $activeClones")
                
                // If no more active clones, stop the service
                if (activeClones.isEmpty()) {
                    Timber.d("No more active clones, stopping service")
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        } else {
            // Stopping the general virtualization layer
            Timber.d("Stopping general virtualization layer")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }
    
    /**
     * Creates the notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Virtualization Service"
            val descriptionText = "Running cloned apps"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Timber.d("Notification channel created")
        }
    }
    
    /**
     * Creates the foreground service notification
     */
    private fun createNotification(): Notification {
        val title = "MultiClone Running"
        val content = if (activeClones.isNotEmpty()) {
            "Running ${activeClones.size} cloned app(s)"
        } else {
            "Virtualization service running"
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
    
    /**
     * Local binder for binding to this service
     */
    inner class LocalBinder : Binder() {
        fun getService(): VirtualizationService = this@VirtualizationService
    }
}