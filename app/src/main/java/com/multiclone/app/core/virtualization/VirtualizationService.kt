package com.multiclone.app.core.virtualization

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground service that handles the virtualization of cloned apps
 */
@AndroidEntryPoint
class VirtualizationService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID = 1000
        private const val CHANNEL_ID = "virtualization_service_channel"
    }
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    // Service scope for coroutines
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Currently active clone ID
    private var activeCloneId: String? = null
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Create notification channel (required for Android 8.0+)
        createNotificationChannel()
        
        // Start as a foreground service
        startForeground(NOTIFICATION_ID, createNotification("Virtual service running"))
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get cloneId and packageName from intent
        val cloneId = intent?.getStringExtra("clone_id")
        val packageName = intent?.getStringExtra("package_name")
        
        if (cloneId != null && packageName != null) {
            // Initialize the virtual environment for this clone
            serviceScope.launch {
                try {
                    // Get the clone information
                    val clone = cloneRepository.clones.firstOrNull()?.find { it.id == cloneId }
                    
                    if (clone != null) {
                        // Update notification with clone display name
                        val notification = createNotification("Running: ${clone.displayName}")
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(NOTIFICATION_ID, notification)
                        
                        // Initialize the virtual environment
                        virtualAppEngine.initializeCloneEnvironment(cloneId, packageName)
                        
                        // Update active clone ID
                        activeCloneId = cloneId
                    }
                } catch (e: Exception) {
                    // Log error
                    e.printStackTrace()
                }
            }
        }
        
        // Keep service running
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Cleanup the active environment
        activeCloneId?.let {
            virtualAppEngine.releaseEnvironment(it)
        }
    }
    
    /**
     * Create the notification channel for this service
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Virtualization Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for the virtualization service notifications"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Create the notification for this service
     */
    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MultiClone App")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}