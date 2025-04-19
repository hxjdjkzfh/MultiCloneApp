package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.core.virtualization.VirtualAppEngineImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing app-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    
    /**
     * Binds the VirtualAppEngine implementation
     */
    @Binds
    @Singleton
    abstract fun bindVirtualAppEngine(
        virtualAppEngineImpl: VirtualAppEngineImpl
    ): VirtualAppEngine
    
    /**
     * Static providers for dependencies
     */
    companion object {
        /**
         * Provides application context
         */
        @Provides
        @Singleton
        fun provideContext(
            @ApplicationContext context: Context
        ): Context = context
    }
}