package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAppRepository(
        @ApplicationContext context: Context
    ): AppRepository {
        return AppRepository(context)
    }
    
    @Provides
    @Singleton
    fun provideCloneRepository(
        @ApplicationContext context: Context,
        virtualAppEngine: VirtualAppEngine,
        appRepository: AppRepository
    ): CloneRepository {
        return CloneRepository(context, virtualAppEngine, appRepository)
    }
}
