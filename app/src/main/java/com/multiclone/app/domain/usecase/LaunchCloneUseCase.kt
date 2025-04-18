package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase for launching a cloned application.
 */
class LaunchCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Execute the use case to launch a clone.
     *
     * @param cloneInfo The CloneInfo of the clone to launch.
     * @return A Result indicating success or failure.
     */
    suspend operator fun invoke(cloneInfo: CloneInfo): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val isLaunched = cloneRepository.launchClone(cloneInfo)
            
            if (isLaunched) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to launch clone"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
