package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase for getting the list of installed applications.
 */
class GetInstalledAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    /**
     * Execute the use case to get installed apps.
     *
     * @param onlyUserApps If true, returns only user-installed apps, excluding system apps.
     * @return List of AppInfo objects representing installed applications.
     */
    suspend operator fun invoke(onlyUserApps: Boolean = true): Result<List<AppInfo>> = withContext(Dispatchers.IO) {
        try {
            val apps = appRepository.getInstalledApps(onlyUserApps)
            Result.success(apps)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Filter apps by search query.
     *
     * @param apps The list of apps to filter.
     * @param query The search query.
     * @return Filtered list of apps.
     */
    fun filterApps(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps
        
        val lowerCaseQuery = query.lowercase()
        return apps.filter {
            it.name.lowercase().contains(lowerCaseQuery) ||
            it.packageName.lowercase().contains(lowerCaseQuery)
        }
    }
}
