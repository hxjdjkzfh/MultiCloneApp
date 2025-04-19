package com.multiclone.app.core.virtualization

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.multiclone.app.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Background service for managing cloned apps
 */
@AndroidEntryPoint
class CloneManagerService : Service() {
    
    companion object {
        private const val TAG = "CloneManagerService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "clone_manager_channel"
    }
    
    @Inject
    lateinit var cloneEnvironment: CloneEnvironment
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        
        // Create notification channel for foreground service
        createNotificationChannel()
        
        // Start as a foreground service to avoid being killed
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MultiClone App")
            .setContentText("Managing clone environments")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null // Not using binding for this service
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Clone Manager"
            val description = "Manages virtual app environments"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Controller class for starting/stopping the service from outside
     */
    @Singleton
    class Controller @Inject constructor(@ApplicationContext private val context: Context) {
        
        /**
         * Start the clone manager service
         */
        fun startService() {
            val intent = Intent(context, CloneManagerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.d(TAG, "Service start requested")
        }
        
        /**
         * Stop the clone manager service
         */
        fun stopService() {
            context.stopService(Intent(context, CloneManagerService::class.java))
            Log.d(TAG, "Service stop requested")
        }
    }
}