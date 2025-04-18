package com.multiclone.app.domain.usecase

import android.util.Log
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.CloneEnvironment
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for launching a cloned app
 */
class LaunchCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine,
    private val cloneEnvironment: CloneEnvironment
) {
    private val TAG = "LaunchCloneUseCase"
    
    /**
     * Launch a cloned app
     * @param cloneId The ID of the clone to launch
     * @return True if launch was successful, false otherwise
     */
    suspend operator fun invoke(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Attempting to launch clone: $cloneId")
            
            // Get clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: run {
                Log.e(TAG, "Clone not found: $cloneId")
                return@withContext false
            }
            
            // Get environment ID for this clone, or create one if it doesn't exist
            val environmentId = cloneEnvironment.getEnvironmentIdForClone(cloneId) ?: run {
                Log.d(TAG, "No environment found for clone $cloneId, creating one...")
                
                // Create a new virtual environment for this clone
                val createResult = virtualAppEngine.createVirtualEnvironment(
                    packageName = cloneInfo.packageName,
                    cloneId = cloneId
                )
                
                if (createResult.isFailure) {
                    Log.e(TAG, "Failed to create virtual environment: ${createResult.exceptionOrNull()?.message}")
                    return@withContext false
                }
                
                createResult.getOrNull() ?: return@withContext false
            }
            
            Log.d(TAG, "Launching clone $cloneId with environment $environmentId")
            
            // Launch the app in its virtual environment
            val launchResult = virtualAppEngine.launchApp(
                cloneId = cloneId,
                environmentId = environmentId,
                packageName = cloneInfo.packageName
            )
            
            // Update last used time if successful
            if (launchResult.isSuccess) {
                Log.d(TAG, "Successfully launched clone $cloneId")
                cloneRepository.updateLastUsed(cloneId)
                
                // Record launch statistics
                val currentLaunchCount = cloneInfo.launchCount ?: 0
                cloneRepository.updateLaunchCount(cloneId, currentLaunchCount + 1)
                
                true
            } else {
                Log.e(TAG, "Failed to launch clone: ${launchResult.exceptionOrNull()?.message}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error launching clone", e)
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Check if a clone can be launched
     * @param cloneId The ID of the clone to check
     * @return True if the clone can be launched, false otherwise
     */
    suspend fun canLaunch(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: return@withContext false
            
            // Check if app is still installed
            val isAppInstalled = true // Replace with actual check when available
            
            // Check if the virtualization engine is available
            val isVirtualizationSupported = virtualAppEngine.isVirtualizationSupported()
            
            isAppInstalled && isVirtualizationSupported
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if clone can be launched", e)
            false
        }
    }
}