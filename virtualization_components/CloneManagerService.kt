package com.multiclone.app.virtualization

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
import androidx.core.app.NotificationCompat
import com.multiclone.app.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Service responsible for managing cloned apps in the background.
 * This service runs as a foreground service to ensure clone management
 * continues even when the app is not in the foreground.
 */
@AndroidEntryPoint
class CloneManagerService : Service() {
    
    @Inject
    lateinit var cloneManager: CloneManager
    
    // Binder for local service binding
    private val binder = LocalBinder()
    
    // Active clone IDs being managed
    private val activeClones = mutableSetOf<String>()
    
    inner class LocalBinder : Binder() {
        fun getService(): CloneManagerService = this@CloneManagerService
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onCreate() {
        super.onCreate()
        
        Timber.d("Clone Manager Service created")
        
        // Create notification channel for Android 8+
        createNotificationChannel()
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createForegroundNotification())
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Clone Manager Service command: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_START_CLONE -> {
                val cloneId = intent.getStringExtra(EXTRA_CLONE_ID) ?: return START_STICKY
                startManagingClone(cloneId)
            }
            ACTION_STOP_CLONE -> {
                val cloneId = intent.getStringExtra(EXTRA_CLONE_ID) ?: return START_STICKY
                stopManagingClone(cloneId)
            }
            ACTION_STOP_SERVICE -> {
                stopForegroundService()
                return START_NOT_STICKY
            }
        }
        
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Clone Manager Service destroyed")
        
        // Update running status for all active clones
        activeClones.forEach { cloneId ->
            cloneManager.updateRunningStatus(cloneId, false)
        }
        
        activeClones.clear()
    }
    
    /**
     * Start managing a clone
     */
    fun startManagingClone(cloneId: String) {
        if (activeClones.contains(cloneId)) {
            Timber.d("Clone $cloneId already being managed")
            return
        }
        
        // Get the clone
        val clone = cloneManager.getClone(cloneId)
        if (clone == null) {
            Timber.e("Attempted to start non-existent clone: $cloneId")
            return
        }
        
        // Add to active set
        activeClones.add(cloneId)
        
        // Update running status
        cloneManager.updateRunningStatus(cloneId, true)
        
        Timber.d("Started managing clone $cloneId: ${clone.displayName}")
        
        // Update notification if needed
        updateForegroundNotification()
    }
    
    /**
     * Stop managing a clone
     */
    fun stopManagingClone(cloneId: String) {
        if (!activeClones.contains(cloneId)) {
            Timber.d("Clone $cloneId not being managed")
            return
        }
        
        // Remove from active set
        activeClones.remove(cloneId)
        
        // Update running status
        cloneManager.updateRunningStatus(cloneId, false)
        
        Timber.d("Stopped managing clone $cloneId")
        
        // Update notification
        updateForegroundNotification()
        
        // If no active clones, consider stopping the service
        if (activeClones.isEmpty()) {
            Timber.d("No active clones, service can be stopped")
            // We don't auto-stop here to allow for potential new clones
        }
    }
    
    /**
     * Stop the foreground service
     */
    private fun stopForegroundService() {
        Timber.d("Stopping foreground service")
        
        // Update all clone statuses
        activeClones.forEach { cloneId ->
            cloneManager.updateRunningStatus(cloneId, false)
        }
        
        // Stop foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        
        // Stop self
        stopSelf()
    }
    
    /**
     * Create notification channel for Android 8+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Clone Manager Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Manages cloned apps in the background"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Create foreground notification
     */
    private fun createForegroundNotification(): Notification {
        // Create intent to return to the app
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create stop action
        val stopIntent = Intent(this, CloneManagerService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MultiClone Active")
            .setContentText(getNotificationText())
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .addAction(0, "Stop Service", stopPendingIntent)
            .build()
    }
    
    /**
     * Update the foreground notification
     */
    private fun updateForegroundNotification() {
        val notification = createForegroundNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * Get text for the notification
     */
    private fun getNotificationText(): String {
        return when (activeClones.size) {
            0 -> "No active clones"
            1 -> "Managing 1 cloned app"
            else -> "Managing ${activeClones.size} cloned apps"
        }
    }
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "multiclone_service_channel"
        
        // Actions
        const val ACTION_START_CLONE = "com.multiclone.app.action.START_CLONE"
        const val ACTION_STOP_CLONE = "com.multiclone.app.action.STOP_CLONE"
        const val ACTION_STOP_SERVICE = "com.multiclone.app.action.STOP_SERVICE"
        
        // Extras
        const val EXTRA_CLONE_ID = "com.multiclone.app.extra.CLONE_ID"
        
        /**
         * Start the service with a clone
         */
        fun startService(context: Context, cloneId: String) {
            val intent = Intent(context, CloneManagerService::class.java).apply {
                action = ACTION_START_CLONE
                putExtra(EXTRA_CLONE_ID, cloneId)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        /**
         * Stop managing a clone
         */
        fun stopManagingClone(context: Context, cloneId: String) {
            val intent = Intent(context, CloneManagerService::class.java).apply {
                action = ACTION_STOP_CLONE
                putExtra(EXTRA_CLONE_ID, cloneId)
            }
            
            context.startService(intent)
        }
        
        /**
         * Stop the service
         */
        fun stopService(context: Context) {
            val intent = Intent(context, CloneManagerService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            
            context.startService(intent)
        }
    }
}