package com.multiclone.app.domain.usecase

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for creating a new app clone
 */
class CreateCloneUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine
) {
    private val packageManager = context.packageManager
    
    /**
     * Create a new clone of an app
     * @param packageName The package name of the app to clone
     * @param cloneName A custom name for the clone
     * @param customIcon Optional custom icon for the clone
     * @return The created CloneInfo or null if creation failed
     */
    suspend operator fun invoke(
        packageName: String,
        cloneName: String,
        customIcon: Bitmap? = null
    ): Result<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            // Get app info
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            
            // Save clone info
            val cloneInfo = cloneRepository.saveClone(
                packageName = packageName,
                originalAppName = appName,
                cloneName = cloneName,
                customIcon = customIcon
            )
            
            // Create virtual environment
            val environmentResult = virtualAppEngine.createVirtualEnvironment(packageName, cloneInfo.id)
            
            if (environmentResult.isSuccess) {
                // Successfully created clone and environment
                Result.success(cloneInfo)
            } else {
                // Failed to create environment, clean up
                cloneRepository.deleteClone(cloneInfo.id)
                Result.failure(environmentResult.exceptionOrNull() ?: Exception("Failed to create virtual environment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}