package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.virtualization.VirtualAppEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the Virtual App Engine that handles app cloning and virtualization.
     */
    @Provides
    @Singleton
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context
    ): VirtualAppEngine {
        return VirtualAppEngine(context)
    }
}