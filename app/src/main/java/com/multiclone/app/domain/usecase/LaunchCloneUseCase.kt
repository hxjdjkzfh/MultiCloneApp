package com.multiclone.app.domain.usecase

import com.multiclone.app.data.repository.CloneRepository
import javax.inject.Inject

/**
 * Use case for launching a cloned application
 */
class LaunchCloneUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Launch a cloned app
     */
    operator fun invoke(cloneId: String) {
        cloneRepository.launchClone(cloneId)
    }
}