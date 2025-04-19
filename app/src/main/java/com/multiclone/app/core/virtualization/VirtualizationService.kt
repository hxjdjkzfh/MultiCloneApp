package com.multiclone.app.core.virtualization

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.multiclone.app.R
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Background service for managing virtualized app environments
 */
@AndroidEntryPoint
class VirtualizationService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "virtualization_service_channel"
    }

    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    // Coroutine scope for background operations
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var monitorJob: Job? = null
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("VirtualizationService - onCreate")
        
        // Create notification channel for foreground service
        createNotificationChannel()
        
        // Start the service in foreground to ensure it stays alive
        startForeground(NOTIFICATION_ID, createNotification(0))
        
        // Start monitoring clones
        startMonitoring()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("VirtualizationService - onStartCommand")
        
        // If the service was killed, restart it
        return START_STICKY
    }
    
    override fun onDestroy() {
        Timber.d("VirtualizationService - onDestroy")
        
        // Cancel the monitoring job
        monitorJob?.cancel()
        
        super.onDestroy()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        // This service doesn't support binding
        return null
    }
    
    /**
     * Create the notification channel for Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.notification_channel_service)
            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = channelName
                enableLights(false)
                enableVibration(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Create the foreground service notification
     */
    private fun createNotification(activeClones: Int): Notification {
        // Intent to open the main activity when notification is tapped
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        // Create the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_running))
            .setContentText(getString(R.string.notification_clones_active, activeClones))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
    
    /**
     * Start monitoring cloned apps
     */
    private fun startMonitoring() {
        monitorJob = serviceScope.launch {
            Timber.d("Starting clone monitoring")
            
            try {
                // Monitor clones and update the notification
                cloneRepository.getClones().collect { clones ->
                    val activeCount = clones.count { it.isRunning }
                    
                    // Update the notification
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, createNotification(activeCount))
                    
                    // Perform periodic maintenance on clone environments
                    performEnvironmentMaintenance()
                    
                    // Wait before next cycle
                    delay(30000) // 30 seconds
                }
            } catch (e: Exception) {
                Timber.e(e, "Error monitoring clones")
            }
        }
    }
    
    /**
     * Perform maintenance on clone environments
     */
    private suspend fun performEnvironmentMaintenance() {
        try {
            // Get clones that need environment updates
            val clonesToUpdate = cloneRepository.getClonesNeedingUpdate()
            
            for (clone in clonesToUpdate) {
                Timber.d("Performing maintenance on clone ${clone.id}")
                
                // Check if the original app is still installed
                if (virtualAppEngine.isCloneRunning(clone.id)) {
                    // Skip maintenance for running clones
                    continue
                }
                
                // Update the environment version
                cloneRepository.updateCloneEnvironmentVersion(clone.id)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error performing environment maintenance")
        }
    }
}