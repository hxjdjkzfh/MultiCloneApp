package com.multiclone.app

import android.app.Application
import android.os.Build
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class
 */
@HiltAndroidApp
class MultiCloneApplication : MultiDexApplication() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("Application created")
        Timber.d("Device: ${Build.MANUFACTURER} ${Build.MODEL}, Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
    }
}