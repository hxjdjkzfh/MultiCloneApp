package com.multiclone.app.domain.usecase

import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for launching a cloned app
 */
class LaunchCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine
) {
    /**
     * Launch a cloned app
     * 
     * @param cloneId The ID of the clone to launch
     * @return A Result containing true if successful, or an exception if failed
     */
    suspend operator fun invoke(cloneId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Get the clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId)
                ?: return@withContext Result.failure(Exception("Clone not found"))
            
            // Launch the clone
            val success = virtualAppEngine.launchClone(cloneInfo)
            
            // Update the last used time if successful
            if (success) {
                cloneRepository.updateLastUsedTime(cloneId)
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to launch clone"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}