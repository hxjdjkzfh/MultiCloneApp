package com.multiclone.app.di

import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.usecase.CreateCloneUseCase
import com.multiclone.app.domain.usecase.CreateShortcutUseCase
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetInstalledAppsUseCase(
        appRepository: AppRepository
    ): GetInstalledAppsUseCase {
        return GetInstalledAppsUseCase(appRepository)
    }
    
    @Provides
    @Singleton
    fun provideCreateCloneUseCase(
        cloneRepository: CloneRepository
    ): CreateCloneUseCase {
        return CreateCloneUseCase(cloneRepository)
    }
    
    @Provides
    @Singleton
    fun provideCreateShortcutUseCase(
        cloneRepository: CloneRepository
    ): CreateShortcutUseCase {
        return CreateShortcutUseCase(cloneRepository)
    }
    
    @Provides
    @Singleton
    fun provideLaunchCloneUseCase(
        cloneRepository: CloneRepository
    ): LaunchCloneUseCase {
        return LaunchCloneUseCase(cloneRepository)
    }
}
