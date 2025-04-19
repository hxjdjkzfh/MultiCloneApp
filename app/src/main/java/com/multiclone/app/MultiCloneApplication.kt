package com.multiclone.app

import android.app.Application
import android.content.Intent
import com.multiclone.app.core.virtualization.CloneManagerService
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class with Hilt dependency injection
 */
@HiltAndroidApp
class MultiCloneApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Start clone manager service
        startCloneManagerService()
    }
    
    /**
     * Start the clone manager service
     */
    private fun startCloneManagerService() {
        val serviceIntent = Intent(this, CloneManagerService::class.java)
        startService(serviceIntent)
    }
}