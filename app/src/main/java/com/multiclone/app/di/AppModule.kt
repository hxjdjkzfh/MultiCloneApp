package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.MultiCloneApplication
import com.multiclone.app.core.virtualization.CloneManagerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Main dependency injection module for the app
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides JSON instance for serialization
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return MultiCloneApplication.json
    }
    
    /**
     * Provides the CloneManagerService wrapper
     */
    @Provides
    @Singleton
    fun provideCloneManagerService(impl: CloneManagerService.Impl): CloneManagerService.Impl {
        return impl
    }
}