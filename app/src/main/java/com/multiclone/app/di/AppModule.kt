package com.multiclone.app.di

import android.content.Context
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
     * Provides the AppRepository instance
     */
    @Singleton
    @Provides
    fun provideAppRepository(
        @ApplicationContext context: Context
    ): AppRepository {
        return AppRepository(context)
    }

    /**
     * Provides the CloneRepository instance
     */
    @Singleton
    @Provides
    fun provideCloneRepository(
        @ApplicationContext context: Context
    ): CloneRepository {
        return CloneRepository(context)
    }
}