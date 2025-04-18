package com.multiclone.app.domain.usecase

import android.graphics.Bitmap
import com.multiclone.app.data.repository.CloneRepository
import javax.inject.Inject

/**
 * Use case for creating a new app clone
 */
class CreateCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppService: com.multiclone.app.domain.service.VirtualAppService
) {
    /**
     * Create a new clone of an app
     * @param packageName the package name of the app to clone
     * @param customName optional custom name for the clone
     * @param customIcon optional custom icon for the clone
     * @return result of the operation
     */
    suspend operator fun invoke(
        packageName: String,
        customName: String? = null,
        customIcon: Bitmap? = null
    ): Result<String> {
        return try {
            // First create virtual environment for the app
            val virtualEnvId = virtualAppService.createVirtualEnvironment(packageName)
            
            // Then register the clone in the repository
            val cloneId = cloneRepository.createClone(
                packageName = packageName,
                virtualEnvId = virtualEnvId,
                customName = customName,
                customIcon = customIcon
            )
            
            Result.success(cloneId)
        } catch (e: Exception) {
            // Clean up any partial setup if necessary
            Result.failure(e)
        }
    }
}