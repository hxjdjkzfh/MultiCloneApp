package com.multiclone.app.domain.usecase

import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.core.virtualization.VirtualAppEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for creating a home screen shortcut for a cloned app
 */
class CreateShortcutUseCase @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine
) {
    /**
     * Create a home screen shortcut for a cloned app
     * 
     * @param cloneId The ID of the clone to create a shortcut for
     * @return True if the shortcut was created successfully, false otherwise
     */
    suspend operator fun invoke(cloneId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Get the clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId)
                ?: return@withContext Result.failure(Exception("Clone not found"))
            
            // Create the shortcut
            val success = virtualAppEngine.createShortcut(cloneInfo)
            
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to create shortcut"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}