package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.data.repository.CloneRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for application-wide dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the clone repository as a singleton
     */
    @Provides
    @Singleton
    fun provideCloneRepository(@ApplicationContext context: Context): CloneRepository {
        return CloneRepository(context)
    }
}