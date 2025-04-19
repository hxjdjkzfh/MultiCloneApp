package com.multiclone.app.core.virtualization

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Service for managing clone operations in the background
 */
@AndroidEntryPoint
class CloneManagerService : Service() {
    
    companion object {
        private const val TAG = "CloneManagerService"
        private const val NOTIFICATION_ID = 2345
        private const val CHANNEL_ID = "clone_manager_channel"
        
        // Actions
        const val ACTION_CREATE_CLONE = "com.multiclone.app.CREATE_CLONE"
        const val ACTION_DELETE_CLONE = "com.multiclone.app.DELETE_CLONE"
        
        // Extras
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_CLONE_ID = "clone_id"
        const val EXTRA_DISPLAY_NAME = "display_name"
    }
    
    // Service scope for background operations
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Binder for local service binding
    private val binder = LocalBinder()
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "CloneManagerService created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "CloneManagerService started with intent: $intent")
        
        // Handle incoming intent
        intent?.let { handleIntent(it, startId) }
        
        // If service is killed while starting, restart with last intent
        return START_REDELIVER_INTENT
    }
    
    /**
     * Handle incoming intents to perform operations
     */
    private fun handleIntent(intent: Intent, startId: Int) {
        val action = intent.action ?: return
        
        when (action) {
            ACTION_CREATE_CLONE -> {
                // Start as foreground service with progress notification
                startForegroundWithProgress("Creating clone...")
                
                // Extract parameters
                val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return
                val displayName = intent.getStringExtra(EXTRA_DISPLAY_NAME) 
                    ?: packageName.split(".").last()
                
                // Launch clone creation task
                serviceScope.launch {
                    try {
                        // Generate clone ID if not provided
                        val cloneId = intent.getStringExtra(EXTRA_CLONE_ID) 
                            ?: java.util.UUID.randomUUID().toString()
                        
                        // Create the clone
                        val success = virtualAppEngine.createClone(packageName, cloneId, displayName)
                        
                        if (success) {
                            Log.d(TAG, "Successfully created clone: $displayName ($cloneId)")
                            showSuccessNotification("Clone created", "$displayName is ready to use")
                        } else {
                            Log.e(TAG, "Failed to create clone: $displayName")
                            showErrorNotification("Clone creation failed", "Could not create $displayName")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error creating clone", e)
                        showErrorNotification("Clone creation failed", e.message ?: "Unknown error")
                    } finally {
                        // Stop the service if no more tasks
                        stopForegroundAndServiceIfDone(startId)
                    }
                }
            }
            
            ACTION_DELETE_CLONE -> {
                // Start as foreground service with progress notification
                startForegroundWithProgress("Deleting clone...")
                
                // Extract parameters
                val cloneId = intent.getStringExtra(EXTRA_CLONE_ID) ?: return
                
                // Launch clone deletion task
                serviceScope.launch {
                    try {
                        // Delete the clone
                        val success = virtualAppEngine.deleteClone(cloneId)
                        
                        if (success) {
                            Log.d(TAG, "Successfully deleted clone: $cloneId")
                            showSuccessNotification("Clone deleted", "The clone has been removed")
                        } else {
                            Log.e(TAG, "Failed to delete clone: $cloneId")
                            showErrorNotification("Deletion failed", "Could not delete the clone")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting clone", e)
                        showErrorNotification("Deletion failed", e.message ?: "Unknown error")
                    } finally {
                        // Stop the service if no more tasks
                        stopForegroundAndServiceIfDone(startId)
                    }
                }
            }
        }
    }
    
    /**
     * Start as foreground service with a progress notification
     */
    private fun startForegroundWithProgress(progressMessage: String) {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Clone Operations",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for clone operations"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create a main activity intent
        val mainActivityIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainActivityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("MultiClone")
            .setContentText(progressMessage)
            .setProgress(0, 0, true) // Indeterminate progress
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, notification)
    }
    
    /**
     * Show a success notification
     */
    private fun showSuccessNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create a main activity intent
        val mainActivityIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainActivityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        // Show the notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    /**
     * Show an error notification
     */
    private fun showErrorNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create a main activity intent
        val mainActivityIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainActivityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        // Show the notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    /**
     * Stop foreground state and, if no more tasks, stop the service
     */
    private fun stopForegroundAndServiceIfDone(startId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        
        // Stop service after task is done
        stopSelf(startId)
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    override fun onDestroy() {
        Log.d(TAG, "CloneManagerService destroyed")
        super.onDestroy()
    }
    
    /**
     * Local binder for service binding
     */
    inner class LocalBinder : Binder() {
        fun getService(): CloneManagerService = this@CloneManagerService
    }
}