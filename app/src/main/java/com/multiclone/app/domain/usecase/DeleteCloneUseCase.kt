package com.multiclone.app.domain.usecase

import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.service.VirtualAppService
import javax.inject.Inject

/**
 * Use case for deleting a cloned app
 */
class DeleteCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppService: VirtualAppService
) {
    /**
     * Delete a clone with the given ID
     * @param cloneId the ID of the clone to delete
     */
    suspend operator fun invoke(cloneId: String) {
        // Get clone info first
        val clone = cloneRepository.getCloneById(cloneId)
        
        // Delete the clone from repository
        cloneRepository.deleteClone(cloneId)
        
        // Cleanup virtual environment
        clone?.virtualEnvironmentId?.let { virtualEnvId ->
            virtualAppService.deleteVirtualEnvironment(virtualEnvId)
        }
    }
}