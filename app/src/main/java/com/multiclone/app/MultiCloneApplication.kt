package com.multiclone.app

import android.app.Application
import com.multiclone.app.core.virtualization.VirtualAppManager
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main application class for MultiClone
 * Handles initialization of repositories and services
 */
@HiltAndroidApp
class MultiCloneApplication : Application() {
    
    @Inject
    lateinit var cloneRepository: CloneRepository
    
    @Inject
    lateinit var virtualAppManager: VirtualAppManager
    
    // Application scope for running coroutines that should live as long as the app
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize repositories
        applicationScope.launch {
            cloneRepository.initialize()
        }
        
        // Start virtualization service
        applicationScope.launch {
            virtualAppManager.startVirtualizationService()
        }
    }
}