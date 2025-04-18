package com.multiclone.app.domain.usecase

import android.graphics.Bitmap
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for creating a new clone of an app
 */
class CreateCloneUseCase @Inject constructor(
    private val appRepository: AppRepository,
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine
) {
    /**
     * Create a new clone of an app
     * 
     * @param packageName The package name of the app to clone
     * @param customName Optional custom name for the clone
     * @param customIcon Optional custom icon for the clone
     * @return A Result containing the created CloneInfo or an exception if failed
     */
    suspend operator fun invoke(
        packageName: String,
        customName: String? = null,
        customIcon: Bitmap? = null
    ): Result<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            // Get the next available clone index for this package
            val cloneIndex = cloneRepository.getNextCloneIndex(packageName)
            
            // Create the clone using the virtualization engine
            val cloneResult = virtualAppEngine.createClone(
                packageName = packageName,
                customName = customName,
                customIcon = customIcon,
                cloneIndex = cloneIndex
            )
            
            if (cloneResult.isSuccess) {
                // Save the clone info
                val cloneInfo = cloneResult.getOrThrow()
                val saved = cloneRepository.saveClone(cloneInfo)
                
                if (saved) {
                    Result.success(cloneInfo)
                } else {
                    Result.failure(Exception("Failed to save clone information"))
                }
            } else {
                cloneResult // Pass through the failure result
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}