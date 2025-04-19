package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.core.virtualization.ClonedAppInstaller
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.core.virtualization.VirtualAppManager
import com.multiclone.app.data.repository.CloneRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for dependency injection of application-level components
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideClonedAppInstaller(
        @ApplicationContext context: Context
    ): ClonedAppInstaller {
        return ClonedAppInstaller(context)
    }
    
    @Provides
    @Singleton
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context,
        clonedAppInstaller: ClonedAppInstaller
    ): VirtualAppEngine {
        return VirtualAppEngine(context, clonedAppInstaller)
    }
    
    @Provides
    @Singleton
    fun provideVirtualAppManager(
        @ApplicationContext context: Context,
        cloneRepository: CloneRepository
    ): VirtualAppManager {
        return VirtualAppManager(context, cloneRepository)
    }
    
    @Provides
    @Singleton
    fun provideCloneRepository(
        @ApplicationContext context: Context,
        virtualAppEngine: VirtualAppEngine
    ): CloneRepository {
        return CloneRepository(context, virtualAppEngine)
    }
}