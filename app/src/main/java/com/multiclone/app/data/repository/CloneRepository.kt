package com.multiclone.app.data.repository

import com.multiclone.app.data.model.CloneInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing cloned applications.
 */
interface CloneRepository {
    /**
     * Observable flow of cloned apps
     */
    val clones: Flow<List<CloneInfo>>
    
    /**
     * Adds a new cloned app to the repository
     * 
     * @param clone The clone information to save
     * @return Success status of the operation
     */
    suspend fun addClone(clone: CloneInfo): Boolean
    
    /**
     * Updates an existing cloned app in the repository
     * 
     * @param clone The updated clone information
     * @return Success status of the operation
     */
    suspend fun updateClone(clone: CloneInfo): Boolean
    
    /**
     * Removes a cloned app from the repository
     * 
     * @param cloneId The ID of the clone to remove
     * @return Success status of the operation
     */
    suspend fun removeClone(cloneId: String): Boolean
    
    /**
     * Gets a specific clone by its ID
     * 
     * @param cloneId The ID of the clone to retrieve
     * @return The clone info or null if not found
     */
    suspend fun getCloneById(cloneId: String): CloneInfo?
    
    /**
     * Loads all clones from storage
     * 
     * @return Success status of the operation
     */
    suspend fun loadClones(): Boolean
}