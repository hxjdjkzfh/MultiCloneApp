package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all installed apps that can be cloned
 */
class GetInstalledAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    /**
     * Get a list of all non-system apps installed on the device
     */
    operator fun invoke(): Flow<List<AppInfo>> {
        return appRepository.getInstalledApps()
    }
}