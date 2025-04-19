package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.core.virtualization.CloneEnvironment
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.core.virtualization.ClonedAppInstaller
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.repository.CloneRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for the app
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the clone repository
     */
    @Provides
    @Singleton
    fun provideCloneRepository(@ApplicationContext context: Context): CloneRepository {
        return CloneRepository(context)
    }
    
    /**
     * Provides the clone environment
     */
    @Provides
    @Singleton
    fun provideCloneEnvironment(@ApplicationContext context: Context): CloneEnvironment {
        return CloneEnvironment(context)
    }
    
    /**
     * Provides the cloned app installer
     */
    @Provides
    @Singleton
    fun provideClonedAppInstaller(@ApplicationContext context: Context): ClonedAppInstaller {
        return ClonedAppInstaller(context)
    }
    
    /**
     * Provides the clone manager service controller
     */
    @Provides
    @Singleton
    fun provideCloneManagerService(@ApplicationContext context: Context): CloneManagerService.Controller {
        return CloneManagerService.Controller(context)
    }
    
    /**
     * Provides the virtual app engine
     */
    @Provides
    @Singleton
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context,
        cloneRepository: CloneRepository,
        cloneEnvironment: CloneEnvironment,
        cloneManagerService: CloneManagerService.Controller
    ): VirtualAppEngine {
        return VirtualAppEngine(context, cloneRepository, cloneEnvironment, cloneManagerService)
    }
}