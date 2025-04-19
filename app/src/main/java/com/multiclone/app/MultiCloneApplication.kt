package com.multiclone.app

import android.app.Application
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.core.virtualization.VirtualizationService
import dagger.hilt.android.HiltAndroidApp
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for MultiClone App
 */
@HiltAndroidApp
class MultiCloneApplication : Application() {
    
    @Inject
    lateinit var virtualizationService: VirtualizationService.Impl
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("MultiClone App initialized")
        
        // Start the virtualization service
        virtualizationService.startService()
        
        // Start the clone manager service
        CloneManagerService.start(this)
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        // Stop the virtualization service
        virtualizationService.stopService()
        
        // Stop the clone manager service
        CloneManagerService.stop(this)
        
        Timber.d("MultiClone App terminated")
    }
    
    companion object {
        /**
         * Singleton JSON instance for serialization/deserialization
         */
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
            encodeDefaults = true
        }
    }
}