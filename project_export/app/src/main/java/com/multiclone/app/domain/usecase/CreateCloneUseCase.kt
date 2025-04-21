package com.multiclone.app.domain.usecase

import android.graphics.Bitmap
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for creating cloned applications
 */
class CreateCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Create a new clone of an app
     */
    suspend operator fun invoke(
        appInfo: AppInfo,
        displayName: String = appInfo.appName,
        customIcon: Bitmap? = null
    ): Result<CloneInfo> = withContext(Dispatchers.IO) {
        try {
            val clone = cloneRepository.createClone(
                packageName = appInfo.packageName,
                displayName = displayName,
                customIcon = customIcon
            )
            Result.success(clone)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}