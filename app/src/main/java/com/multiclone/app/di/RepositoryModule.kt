package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.data.repository.AppRepository
import com.multiclone.app.data.repository.AppRepositoryImpl
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.data.repository.CloneRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger/Hilt module for providing repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides the AppRepository implementation.
     */
    @Provides
    @Singleton
    fun provideAppRepository(
        @ApplicationContext context: Context
    ): AppRepository {
        return AppRepositoryImpl(context)
    }

    /**
     * Provides the CloneRepository implementation.
     */
    @Provides
    @Singleton
    fun provideCloneRepository(
        @ApplicationContext context: Context
    ): CloneRepository {
        return CloneRepositoryImpl(context)
    }
}