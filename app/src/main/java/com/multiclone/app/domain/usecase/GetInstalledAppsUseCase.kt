package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case that retrieves all non-system apps installed on the device
 */
class GetInstalledAppsUseCase @Inject constructor(
    private val virtualAppEngine: VirtualAppEngine
) {
    /**
     * Execute the use case to get all installed non-system apps
     * 
     * @param includeSystemApps Whether to include system apps in the results (default: false)
     * @return A flow that emits a list of AppInfo objects
     */
    operator fun invoke(includeSystemApps: Boolean = false): Flow<List<AppInfo>> = flow {
        val apps = virtualAppEngine.getInstalledApps()
        val filteredApps = if (includeSystemApps) {
            apps
        } else {
            apps.filter { !it.isSystem }
        }
        
        emit(filteredApps)
    }
}