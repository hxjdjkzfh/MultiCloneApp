package com.multiclone.app.core.virtualization

import com.multiclone.app.data.model.CloneInfo
import java.io.File

/**
 * Interface defining operations for managing virtual environments for cloned apps.
 * This component is responsible for creating, validating, and managing the
 * isolated environment where cloned apps run.
 */
interface CloneEnvironment {
    /**
     * Initializes a clone environment in the specified directory.
     *
     * @param cloneDir The directory where the clone environment will be created
     * @param cloneInfo Information about the clone being created
     * @return Success status of the operation
     */
    suspend fun initialize(cloneDir: File, cloneInfo: CloneInfo): Boolean
    
    /**
     * Checks if the clone environment is valid and properly configured.
     *
     * @param cloneDir The directory containing the clone environment
     * @param cloneInfo Information about the clone
     * @return True if the environment is valid and ready to use
     */
    suspend fun isValid(cloneDir: File, cloneInfo: CloneInfo): Boolean
    
    /**
     * Updates the settings of an existing clone environment.
     *
     * @param cloneDir The directory containing the clone environment
     * @param cloneInfo Updated information about the clone
     * @return Success status of the operation
     */
    suspend fun updateSettings(cloneDir: File, cloneInfo: CloneInfo): Boolean
    
    /**
     * Performs cleanup operations on a clone environment.
     *
     * @param cloneInfo Information about the clone to clean up
     * @return Success status of the operation
     */
    suspend fun cleanup(cloneInfo: CloneInfo): Boolean
}