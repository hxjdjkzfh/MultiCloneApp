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
     * @param cloneId The ID of the clone to launch
     * @return True if launch was successful, false otherwise
     */
    suspend operator fun invoke(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: return@withContext false
            
            // Launch the app
            val result = virtualAppEngine.launchApp(
                cloneId = cloneId,
                environmentId = cloneId, // Environment ID same as clone ID
                packageName = cloneInfo.packageName
            )
            
            // Update last used time if successful
            if (result.isSuccess) {
                cloneRepository.updateLastUsed(cloneId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}