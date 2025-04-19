package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.core.virtualization.VirtualAppEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger/Hilt module for providing virtualization components.
 */
@Module
@InstallIn(SingletonComponent::class)
object VirtualizationModule {

    /**
     * Provides the VirtualAppEngine singleton.
     */
    @Provides
    @Singleton
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context
    ): VirtualAppEngine {
        return VirtualAppEngine(context)
    }
}