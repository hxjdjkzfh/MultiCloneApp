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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that manages the virtualization environment
 */
class VirtualizationService : Service() {
    
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val binder = VirtualizationBinder()
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var cloneEnvironment: CloneEnvironment
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("VirtualizationService created")
        
        startForeground()
        
        // Initialize the virtualization environment
        serviceScope.launch {
            try {
                cloneEnvironment.initialize()
                Timber.d("Virtualization environment initialized")
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize virtualization environment")
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("VirtualizationService started")
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder {
        Timber.d("VirtualizationService bound")
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Timber.d("VirtualizationService destroyed")
    }
    
    /**
     * Create a foreground notification for the service
     */
    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "virtualization_service"
            val channelName = "Virtualization Service"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used to run cloned apps"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            
            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("MultiClone App")
                .setContentText("Virtualization service is running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
            
            startForeground(NOTIFICATION_ID, notification)
        }
    }
    
    /**
     * Binder class for service binding
     */
    inner class VirtualizationBinder : Binder() {
        fun getService(): VirtualizationService = this@VirtualizationService
    }
    
    /**
     * Class used for injection in other components
     */
    @Singleton
    class Impl @Inject constructor(
        private val context: Context
    ) {
        /**
         * Start the virtualization service
         */
        fun startService() {
            val intent = Intent(context, VirtualizationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        /**
         * Stop the virtualization service
         */
        fun stopService() {
            val intent = Intent(context, VirtualizationService::class.java)
            context.stopService(intent)
        }
    }
    
    companion object {
        private const val NOTIFICATION_ID = 12345
    }
}