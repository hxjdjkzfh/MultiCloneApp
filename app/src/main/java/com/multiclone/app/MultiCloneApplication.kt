package com.multiclone.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class for MultiClone.
 * Initializes app-wide components like Timber for logging and Hilt for dependency injection.
 */
@HiltAndroidApp
class MultiCloneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.i("MultiClone application initialized")
    }
}