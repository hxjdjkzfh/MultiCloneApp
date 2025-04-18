package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.utils.IconUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide repository-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAppRepository(
        @ApplicationContext context: Context,
        iconUtils: IconUtils
    ): AppRepository {
        return AppRepository(context, iconUtils)
    }
    
    @Provides
    @Singleton
    fun provideCloneRepository(
        @ApplicationContext context: Context
    ): CloneRepository {
        return CloneRepository(context)
    }
}