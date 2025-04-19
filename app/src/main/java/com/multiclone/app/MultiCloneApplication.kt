package com.multiclone.app

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.multiclone.app.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MultiCloneApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialize virtualization engine
        initVirtualizationEngine()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    
    private fun initVirtualizationEngine() {
        Timber.d("Initializing virtualization engine...")
        // VirtualAppEngine initialization will be implemented here
    }
    
    companion object {
        const val TAG = "MultiCloneApp"
    }
}