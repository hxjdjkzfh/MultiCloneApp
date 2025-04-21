package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.virtualization.CloneEnvironment
import com.multiclone.app.virtualization.CloneEnvironmentImpl
import com.multiclone.app.virtualization.VirtualAppEngine
import com.multiclone.app.virtualization.VirtualAppEngineImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides virtualization dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object VirtualizationModule {
    
    /**
     * Provides VirtualAppEngine implementation.
     * 
     * @param context The application context
     * @return An implementation of VirtualAppEngine
     */
    @Provides
    @Singleton
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context
    ): VirtualAppEngine {
        return VirtualAppEngineImpl(context)
    }
    
    /**
     * Provides CloneEnvironment implementation.
     * 
     * @param context The application context
     * @param virtualAppEngine The virtual app engine
     * @return An implementation of CloneEnvironment
     */
    @Provides
    @Singleton
    fun provideCloneEnvironment(
        @ApplicationContext context: Context,
        virtualAppEngine: VirtualAppEngine
    ): CloneEnvironment {
        return CloneEnvironmentImpl(context, virtualAppEngine)
    }
}