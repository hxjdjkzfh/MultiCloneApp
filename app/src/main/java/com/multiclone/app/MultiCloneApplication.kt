package com.multiclone.app

import android.app.Application
import android.content.Intent
import androidx.multidex.MultiDexApplication
import com.multiclone.app.core.virtualization.CloneManagerService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class for MultiClone App.
 * Initializes essential components for the app.
 */
@HiltAndroidApp
class MultiCloneApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Start the clone manager service
        startCloneManagerService()
    }
    
    /**
     * Starts the background service that manages app clones
     */
    private fun startCloneManagerService() {
        try {
            val intent = Intent(this, CloneManagerService::class.java)
            startService(intent)
            Timber.d("Clone Manager Service started successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to start Clone Manager Service")
        }
    }
    
    override fun onTerminate() {
        // Clean up resources
        Timber.d("Application terminating")
        super.onTerminate()
    }
}