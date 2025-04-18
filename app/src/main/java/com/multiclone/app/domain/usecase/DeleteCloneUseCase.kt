package com.multiclone.app.domain.usecase

import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for deleting a cloned application
 */
class DeleteCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Delete a cloned app by ID
     */
    suspend operator fun invoke(cloneId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val result = cloneRepository.deleteClone(cloneId)
            if (result) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete clone"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}