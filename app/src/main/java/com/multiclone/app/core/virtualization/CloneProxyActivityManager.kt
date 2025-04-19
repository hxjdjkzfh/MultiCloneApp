package com.multiclone.app.core.virtualization

import android.content.Intent
import com.multiclone.app.data.model.CloneInfo

/**
 * Interface defining operations for managing proxy activities for cloned apps.
 * Proxy activities are used to intercept and redirect activity launches
 * to maintain isolation between clones.
 */
interface CloneProxyActivityManager {
    /**
     * Registers proxy activities for a cloned app.
     *
     * @param packageName The package name of the original app
     * @param cloneInfo Information about the clone
     * @return Success status of the operation
     */
    suspend fun registerProxies(packageName: String, cloneInfo: CloneInfo): Boolean
    
    /**
     * Unregisters proxy activities for a cloned app.
     *
     * @param cloneInfo Information about the clone
     * @return Success status of the operation
     */
    suspend fun unregisterProxies(cloneInfo: CloneInfo): Boolean
    
    /**
     * Gets the launch intent for a cloned app.
     *
     * @param cloneInfo Information about the clone to launch
     * @return The intent to start the cloned app or null if not available
     */
    suspend fun getLaunchIntent(cloneInfo: CloneInfo): Intent?
}