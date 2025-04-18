package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for retrieving installed applications
 */
class GetInstalledAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    /**
     * Get all installed apps that can be cloned
     */
    suspend operator fun invoke(): List<AppInfo> = withContext(Dispatchers.IO) {
        appRepository.getInstalledApps()
    }
    
    /**
     * Search for apps by name or package
     */
    suspend fun search(query: String): List<AppInfo> = withContext(Dispatchers.IO) {
        appRepository.searchInstalledApps(query)
    }
}