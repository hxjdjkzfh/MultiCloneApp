package com.multiclone.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MultiCloneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide components here
    }
}
