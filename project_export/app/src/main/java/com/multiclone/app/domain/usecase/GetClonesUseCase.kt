package com.multiclone.app.domain.usecase

import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for retrieving cloned applications
 */
class GetClonesUseCase @Inject constructor(
    private val cloneRepository: CloneRepository
) {
    /**
     * Get all clones
     */
    suspend operator fun invoke(): List<CloneInfo> = withContext(Dispatchers.IO) {
        cloneRepository.getClones()
    }
    
    /**
     * Get a single clone by ID
     */
    suspend fun getClone(cloneId: String): CloneInfo? = withContext(Dispatchers.IO) {
        cloneRepository.getClone(cloneId)
    }
    
    /**
     * Observe changes to the clones list
     * This would be implemented with a StateFlow in a real app
     */
    fun observeClones(): Flow<List<CloneInfo>> = flow {
        emit(cloneRepository.getClones())
    }.flowOn(Dispatchers.IO)
}