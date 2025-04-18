package com.multiclone.app.domain.usecase

import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.service.VirtualAppService
import javax.inject.Inject

/**
 * Use case for launching a cloned app
 */
class LaunchCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppService: VirtualAppService
) {
    /**
     * Launch a specific clone
     * @param cloneId the ID of the clone to launch
     */
    suspend operator fun invoke(cloneId: String) {
        // Get clone info from repository
        val clone = cloneRepository.getCloneById(cloneId)
        
        // Update last used timestamp
        cloneRepository.updateLastUsedTime(cloneId)
        
        // Launch the clone in virtual environment
        clone?.let {
            virtualAppService.launchApp(it.packageName, it.virtualEnvironmentId)
        }
    }
}