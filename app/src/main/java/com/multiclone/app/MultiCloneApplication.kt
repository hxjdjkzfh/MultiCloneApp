package com.multiclone.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for MultiClone
 * 
 * Used for Hilt dependency injection initialization
 */
@HiltAndroidApp
class MultiCloneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize app-wide resources here if needed
    }
}