package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger/Hilt module that provides application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the VirtualAppEngine instance
     */
    @Singleton
    @Provides
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context
    ): VirtualAppEngine {
        return VirtualAppEngine(context)
    }

    /**
     * Provides the AppRepository instance
     */
    @Singleton
    @Provides
    fun provideAppRepository(
        virtualAppEngine: VirtualAppEngine
    ): AppRepository {
        return AppRepository(virtualAppEngine)
    }

    /**
     * Provides the CloneRepository instance
     */
    @Singleton
    @Provides
    fun provideCloneRepository(
        @ApplicationContext context: Context,
        virtualAppEngine: VirtualAppEngine
    ): CloneRepository {
        return CloneRepository(context, virtualAppEngine)
    }
    
    /**
     * Provides the VirtualAppService instance
     */
    @Singleton
    @Provides
    fun provideVirtualAppService(
        @ApplicationContext context: Context
    ): com.multiclone.app.core.service.VirtualAppService {
        return com.multiclone.app.core.service.VirtualAppService(context)
    }
}