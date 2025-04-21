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
 * Hilt module that provides repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * Provides AppRepository implementation.
     * 
     * @param context The application context
     * @return An implementation of AppRepository
     */
    @Provides
    @Singleton
    fun provideAppRepository(
        @ApplicationContext context: Context
    ): AppRepository {
        return AppRepositoryImpl(context)
    }
    
    /**
     * Provides CloneRepository implementation.
     * 
     * @param context The application context
     * @return An implementation of CloneRepository
     */
    @Provides
    @Singleton
    fun provideCloneRepository(
        @ApplicationContext context: Context
    ): CloneRepository {
        return CloneRepositoryImpl(context)
    }
}