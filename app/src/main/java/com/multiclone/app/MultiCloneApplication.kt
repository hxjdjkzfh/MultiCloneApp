package com.multiclone.app

import android.app.Application
import android.content.Intent
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.domain.service.VirtualAppService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Main Application class that initializes app-wide dependencies
 */
@HiltAndroidApp
class MultiCloneApplication : Application() {

    @Inject
    lateinit var virtualAppService: VirtualAppService

    override fun onCreate() {
        super.onCreate()
        
        // Start the CloneManagerService
        startService(Intent(this, CloneManagerService::class.java))
        
        // Connect to the service
        virtualAppService.connect()
    }
    
    override fun onTerminate() {
        // Disconnect from the service
        virtualAppService.disconnect()
        super.onTerminate()
    }
}