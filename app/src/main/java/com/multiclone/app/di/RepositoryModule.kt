package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.core.virtualization.CloneEnvironment
import com.multiclone.app.core.virtualization.ClonedAppInstaller
import com.multiclone.app.core.virtualization.VirtualAppEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger/Hilt module that provides repository and virtualization dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides the CloneEnvironment instance
     */
    @Singleton
    @Provides
    fun provideCloneEnvironment(
        @ApplicationContext context: Context
    ): CloneEnvironment {
        return CloneEnvironment(context)
    }

    /**
     * Provides the ClonedAppInstaller instance
     */
    @Singleton
    @Provides
    fun provideClonedAppInstaller(
        @ApplicationContext context: Context
    ): ClonedAppInstaller {
        return ClonedAppInstaller(context)
    }

    /**
     * Provides the VirtualAppEngine instance
     */
    @Singleton
    @Provides
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context,
        cloneEnvironment: CloneEnvironment,
        clonedAppInstaller: ClonedAppInstaller
    ): VirtualAppEngine {
        return VirtualAppEngine(
            context = context,
            cloneEnvironment = cloneEnvironment,
            clonedAppInstaller = clonedAppInstaller
        )
    }
}