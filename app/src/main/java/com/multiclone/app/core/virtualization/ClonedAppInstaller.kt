package com.multiclone.app.core.virtualization

import com.multiclone.app.data.model.CloneInfo
import java.io.File

/**
 * Interface defining operations for installing cloned applications.
 * This component is responsible for copying and configuring app resources
 * into the virtual environment.
 */
interface ClonedAppInstaller {
    /**
     * Installs a cloned app in the specified directory.
     *
     * @param packageName The package name of the original app
     * @param cloneDir The directory containing the clone environment
     * @param cloneInfo Information about the clone being created
     * @return Success status of the operation
     */
    suspend fun install(packageName: String, cloneDir: File, cloneInfo: CloneInfo): Boolean
}