package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting installed apps on the device
 */
class GetInstalledAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    /**
     * Execute the use case to get all installed apps
     * @param includeSystemApps whether to include system apps in the results
     * @return a flow of app info list
     */
    operator fun invoke(includeSystemApps: Boolean = false): Flow<List<AppInfo>> {
        return appRepository.getInstalledApps(includeSystemApps)
    }
}