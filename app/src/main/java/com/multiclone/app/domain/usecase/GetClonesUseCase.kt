package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all cloned apps
 */
class GetClonesUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Execute the use case to get all clones
     * @return a flow of clone info list
     */
    operator fun invoke(): Flow<List<CloneInfo>> {
        return cloneRepository.getAllClones()
    }
}