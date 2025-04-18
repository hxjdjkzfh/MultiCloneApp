package com.multiclone.app.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase for creating a clone of an installed application.
 */
class CreateCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Execute the use case to create a clone.
     *
     * @param packageName The package name of the app to clone.
     * @param cloneName The custom name for the cloned app.
     * @param customIcon Optional custom icon for the cloned app.
     * @return A Result containing the created CloneInfo if successful, or an error if failed.
     */
    suspend operator fun invoke(
        packageName: String,
        cloneName: String,
        customIcon: Bitmap? = null
    ): Result<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            val cloneInfo = cloneRepository.createClone(
                packageName = packageName,
                cloneName = cloneName,
                customIcon = customIcon
            )
            
            if (cloneInfo != null) {
                Result.success(cloneInfo)
            } else {
                Result.failure(Exception("Failed to create clone"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Validate clone creation parameters.
     *
     * @param packageName The package name of the app to clone.
     * @param cloneName The custom name for the cloned app.
     * @return A validation error message if invalid, null if valid.
     */
    fun validateCloneParams(packageName: String, cloneName: String): String? {
        if (packageName.isBlank()) {
            return "Package name is required"
        }
        
        if (cloneName.isBlank()) {
            return "Clone name is required"
        }
        
        if (cloneName.length < 3) {
            return "Clone name must be at least 3 characters"
        }
        
        return null
    }
}
