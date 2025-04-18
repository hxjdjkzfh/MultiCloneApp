package com.multiclone.app.di

import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.usecase.CreateCloneUseCase
import com.multiclone.app.domain.usecase.CreateShortcutUseCase
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Dagger/Hilt module that provides use case dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * Provides the GetInstalledAppsUseCase instance
     */
    @Provides
    fun provideGetInstalledAppsUseCase(
        virtualAppEngine: VirtualAppEngine
    ): GetInstalledAppsUseCase {
        return GetInstalledAppsUseCase(virtualAppEngine)
    }

    /**
     * Provides the CreateCloneUseCase instance
     */
    @Provides
    fun provideCreateCloneUseCase(
        appRepository: AppRepository,
        cloneRepository: CloneRepository,
        virtualAppEngine: VirtualAppEngine
    ): CreateCloneUseCase {
        return CreateCloneUseCase(
            appRepository,
            cloneRepository,
            virtualAppEngine
        )
    }

    /**
     * Provides the CreateShortcutUseCase instance
     */
    @Provides
    fun provideCreateShortcutUseCase(
        cloneRepository: CloneRepository,
        virtualAppEngine: VirtualAppEngine
    ): CreateShortcutUseCase {
        return CreateShortcutUseCase(
            cloneRepository = cloneRepository,
            virtualAppEngine = virtualAppEngine
        )
    }

    /**
     * Provides the LaunchCloneUseCase instance
     */
    @Provides
    fun provideLaunchCloneUseCase(
        cloneRepository: CloneRepository,
        virtualAppEngine: VirtualAppEngine
    ): LaunchCloneUseCase {
        return LaunchCloneUseCase(
            cloneRepository = cloneRepository,
            virtualAppEngine = virtualAppEngine
        )
    }
}