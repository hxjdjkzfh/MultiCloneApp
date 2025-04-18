package com.multiclone.app.domain.usecase

import android.content.Context
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase for creating a home screen shortcut for a cloned application.
 */
class CreateShortcutUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Execute the use case to create a shortcut.
     *
     * @param cloneInfo The CloneInfo of the clone to create a shortcut for.
     * @param context The application context.
     * @return A Result indicating success or failure.
     */
    suspend operator fun invoke(
        cloneInfo: CloneInfo,
        context: Context
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val isCreated = cloneRepository.createShortcut(cloneInfo, context)
            
            if (isCreated) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to create shortcut"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
