package com.multiclone.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class for MultiClone.
 * Initializes application-wide components like Dependency Injection,
 * Logging, and other global configurations.
 */
@HiltAndroidApp
class MultiCloneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Setup logging for debug builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Logging initialized in debug mode")
        }
        
        // Initialize app components
        initializeComponents()
    }
    
    /**
     * Add MultiDex support for pre-Android 5.0 devices
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    
    /**
     * Initialize any components needed throughout the app
     */
    private fun initializeComponents() {
        Timber.d("Initializing application components")
        
        // Initialize database if needed
        
        // Initialize work manager for background tasks if needed
        
        // Initialize crash reporting if needed
    }
}