package com.multiclone.app.domain.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.multiclone.app.core.virtualization.CloneManagerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service interface for connecting to CloneManagerService
 * This provides a way for the application to communicate with the background service
 */
@Singleton
class VirtualAppService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cloneManagerService: CloneManagerService? = null
    private var isBound = false
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CloneManagerService.LocalBinder
            cloneManagerService = binder.getService()
            isBound = true
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            cloneManagerService = null
            isBound = false
        }
    }
    
    /**
     * Connect to the service
     */
    fun connect() {
        if (!isBound) {
            val intent = Intent(context, CloneManagerService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    
    /**
     * Disconnect from the service
     */
    fun disconnect() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }
    
    /**
     * Check if the service is connected
     */
    fun isConnected(): Boolean {
        return isBound && cloneManagerService != null
    }
    
    /**
     * Get the environment for a specific clone
     */
    fun getEnvironment(packageName: String, cloneId: String, displayName: String) {
        cloneManagerService?.getEnvironment(packageName, cloneId, displayName)
    }
    
    /**
     * Release an environment for a specific clone
     */
    fun releaseEnvironment(cloneId: String) {
        cloneManagerService?.releaseEnvironment(cloneId)
    }
}